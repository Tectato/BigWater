package bigwater;

import bigwater.config.SimpleConfig;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BigWater implements ClientModInitializer {
	public static final String MOD_ID = "bigwater";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final SimpleConfig CONFIG = SimpleConfig.of(MOD_ID).provider( BigWater::provider ).request();

	public static final String VAR_DEFAULTSCALE = "defaultTextureScale";
	public static final String VAR_OVERRIDE = "override";
	public static int defaultTextureScale = CONFIG.getOrDefault(VAR_DEFAULTSCALE, 1);
	public static float defaultScalant = 1.0f/defaultTextureScale;
	public static boolean override = CONFIG.getOrDefault(VAR_OVERRIDE, false);

	public static Map<String, Tuple<Integer, Float>> textureScales = HashMap.newHashMap(8);
	private static List<String> failedLookups = new LinkedList<>();

	public static Map<String, TextureAtlasSprite> fluidTextures = HashMap.newHashMap(8);

	@Override
	public void onInitializeClient() {
		Identifier rekindled = Identifier.fromNamespaceAndPath(MOD_ID,"rekindled");
		Identifier stylized = Identifier.fromNamespaceAndPath(MOD_ID,"stylized");
		Identifier vanilla = Identifier.fromNamespaceAndPath(MOD_ID,"vanilla");
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
			ResourceLoader.registerBuiltinPack(rekindled, container, PackActivationType.NORMAL);
			ResourceLoader.registerBuiltinPack(stylized, container, PackActivationType.NORMAL);
			ResourceLoader.registerBuiltinPack(vanilla, container, PackActivationType.DEFAULT_ENABLED);
		});

		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				Identifier.fromNamespaceAndPath(MOD_ID,"config"),
				(ResourceManagerReloadListener) manager -> {
					textureScales.clear();
					Map<Identifier, Resource> resourceMap = manager.listResources("config", path -> path.toString().endsWith("bigwater.json"));

					for(Map.Entry<Identifier, Resource> entry : resourceMap.entrySet()){
						try(InputStream stream = manager.getResource(entry.getKey()).get().open()) {
							BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
							JsonObject json = GsonHelper.parse(streamReader);

							JsonObject settings = json.get("textureScale").getAsJsonObject();
							for (String key : settings.keySet()){
								int value = settings.get(key).getAsInt();
								textureScales.put(key, new Tuple<>(value, 1.0f/value));
								String[] split = key.split(":");
								if (split.length > 1) {
									textureScales.put(split[0] + ":flowing_" + split[1], new Tuple<>(value, 1.0f / value));
								}
							}
							LOGGER.info("[BigWater] Read resource pack provided settings");

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
		if (override){
			return new Tuple<>(defaultTextureScale, defaultScalant);
		}
		if (textureScales.containsKey(identifier)){
			return textureScales.get(identifier);
		}
		if (!failedLookups.contains(identifier)){
			failedLookups.add(identifier);
			LOGGER.info("[BigWater] Scale lookup failed for " + identifier + ", using config default");
		}
		return new Tuple<>(defaultTextureScale, defaultScalant);
	}

	public static TextureAtlasSprite getTexture(String identifier){
		if (fluidTextures.containsKey(identifier)){
			return fluidTextures.get(identifier);
		}
		if (!failedLookups.contains(identifier)){
			failedLookups.add(identifier);
			LOGGER.info("[BigWater] Texture lookup failed for " + identifier + ", using default");
		}
		return null;
	}

	public static void setConfig(String key, String value){
		CONFIG.set(key, value);

		if (key.equals(VAR_DEFAULTSCALE)){
			defaultTextureScale = CONFIG.getOrDefault(key, 1);
			defaultScalant = 1.0f/defaultTextureScale;
		} else if (key.equals(VAR_OVERRIDE)){
			override = Boolean.parseBoolean(value);
		}
	}

	public static void writeConfig(){
		CONFIG.writeToFile();
	}

	private static String provider( String filename ) {
		return "# Default scale for textures if resourcepacks don't provide any:\n"
				+ VAR_DEFAULTSCALE + "=1"
				+ "\n\n# Override pack-provided settings with default scale:\n"
				+ VAR_OVERRIDE + "=false";
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