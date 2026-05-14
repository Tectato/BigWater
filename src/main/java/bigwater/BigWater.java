package bigwater;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static bigwater.BigWater.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.CLIENT)
public class BigWater {
	public static final String MOD_ID = "bigwater";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Map<String, Tuple<Integer, Float>> textureScales = HashMap.newHashMap(8);
	private static final List<String> failedLookups = new LinkedList<>();

	public static Map<String, TextureAtlasSprite> fluidTextures = HashMap.newHashMap(8);

	public BigWater(IEventBus eventBus, ModContainer modContainer) {
		eventBus.addListener(this::addPackFinders);
		eventBus.addListener(this::addReloadListeners);

		modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}

	private void addReloadListeners(final AddClientReloadListenersEvent event) {
		event.addListener(
				Identifier.fromNamespaceAndPath(MOD_ID,"config"),
				(ResourceManagerReloadListener)this::onReload
		);
	}

	private void onReload(ResourceManager manager) {
		textureScales.clear();
		failedLookups.clear();
		Map<Identifier, Resource> resourceMap = manager.listResources("config", path -> path.toString().endsWith(".json"));

		for(Map.Entry<Identifier, Resource> entry : resourceMap.entrySet()){
			try(InputStream stream = manager.getResource(entry.getKey()).get().open()) {
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
				JsonObject json = GsonHelper.parse(streamReader);

				try {
					int scale = json.get("textureScale").getAsInt();
					String id = String.valueOf(entry.getKey());
					id = id.substring("bigwater:config/".length(),id.length() - 5);
					Tuple<Integer, Float> values = new Tuple<>(scale, 1.0f/scale);
					textureScales.put("minecraft:" + id, values);
					textureScales.put("minecraft:flowing_" + id, values);
				} catch (Exception e){}

			} catch(Exception e) {
				LOGGER.error("[BigWater] Failed to read resource pack settings");
				LOGGER.error(String.valueOf(e));
			}
		}

		fluidTextures.clear();
		checkCustomTextures("water"); // TODO: make this run for any registered fluids
		checkCustomTextures("lava");

		/*for(String key : fluidTextures.keySet()){
			LOGGER.info("[BW] " + key + " -> " + fluidTextures.get(key));
		}*/
	}

	private void addPackFinders(final AddPackFindersEvent event) {
		if(event.getPackType() != PackType.CLIENT_RESOURCES) return;

		event.addPackFinders(
				Identifier.fromNamespaceAndPath(MOD_ID, "resourcepacks/vanilla"),
				PackType.CLIENT_RESOURCES,
				Component.literal("Big Water"),
				PackSource.BUILT_IN,
				false,
				Pack.Position.TOP
		);

		event.addPackFinders(
				Identifier.fromNamespaceAndPath(MOD_ID, "resourcepacks/rekindled"),
				PackType.CLIENT_RESOURCES,
				Component.literal("Big Water Rekindled"),
				PackSource.BUILT_IN,
				false,
				Pack.Position.TOP
		);

		event.addPackFinders(
				Identifier.fromNamespaceAndPath(MOD_ID, "resourcepacks/stylized"),
				PackType.CLIENT_RESOURCES,
				Component.literal("Big Water Stylized"),
				PackSource.BUILT_IN,
				false,
				Pack.Position.TOP
		);
	}

	private static void checkCustomTextures(String blockID){
		TextureAtlasSprite stillSprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(Identifier.fromNamespaceAndPath(MOD_ID,"block/" + blockID + "_still"));
		if (stillSprite.contents().name().toString().equals("minecraft:missingno")) stillSprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(Identifier.fromNamespaceAndPath("minecraft","block/" + blockID + "_still"));
		fluidTextures.put("minecraft:block/"+blockID+"_still", stillSprite);
		TextureAtlasSprite flowSprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(Identifier.fromNamespaceAndPath(MOD_ID,"block/" + blockID + "_flow"));
		if (flowSprite.contents().name().toString().equals("minecraft:missingno")) flowSprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(Identifier.fromNamespaceAndPath("minecraft","block/" + blockID + "_flow"));
		fluidTextures.put("minecraft:block/"+blockID+"_flow", flowSprite);
	}

	public static Tuple<Integer, Float> getTextureScale(String identifier){
		if (Config.FORCE_FALLBACK_SCALE.get()){
			return new Tuple<>(Config.FALLBACK_SCALE.get(), 1.0f/Config.FALLBACK_SCALE.get());
		}

		if (textureScales.containsKey(identifier)){
			return textureScales.get(identifier);
		}
		if (!failedLookups.contains(identifier)){
			failedLookups.add(identifier);
			LOGGER.info("[BigWater] Scale lookup failed for {}, using config default", identifier);
		}
		return new Tuple<>(Config.FALLBACK_SCALE.get(), 1.0f/Config.FALLBACK_SCALE.get());
	}

	public static TextureAtlasSprite getTexture(String identifier){
		if (fluidTextures.containsKey(identifier)){
			return fluidTextures.get(identifier);
		}
		if (!failedLookups.contains(identifier)){
			failedLookups.add(identifier);
			LOGGER.info("[BigWater] Texture lookup failed for {}, using default", identifier);
		}
		return null;
	}

	public static int getTexPos(int worldPos, int textureScale, boolean reverseCoords){
		int texPos = worldPos % textureScale;
		if (texPos < 0) texPos = textureScale + texPos;
		if (reverseCoords) texPos = reverseCoord(texPos, textureScale);
		return texPos;
	}

	public static float modCoord(float src, int relativePos, float origin, float sideLength, float scalant){
		return (((src + (sideLength * relativePos) - origin) * scalant)) + origin;
	}

	public static int reverseCoord(int pos, int textureScale){
		return (textureScale - pos) - 1;
	}
}