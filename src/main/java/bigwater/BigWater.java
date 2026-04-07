package bigwater;

import bigwater.config.SimpleConfig;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
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
import java.util.Map;

public class BigWater implements ClientModInitializer {
	public static final String MOD_ID = "bigwater";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final SimpleConfig CONFIG = SimpleConfig.of(MOD_ID).provider( BigWater::provider ).request();

	public static final String VAR_DEFAULTSCALE = "defaultTextureScale";
	public static int defaultTextureScale = CONFIG.getOrDefault(VAR_DEFAULTSCALE, 1);
	public static float defaultScalant = 1.0f/defaultTextureScale;

	public static Map<String, Tuple<Integer, Float>> textureScales = HashMap.newHashMap(8);

	@Override
	public void onInitializeClient() {
		Identifier rekindled = Identifier.fromNamespaceAndPath(MOD_ID,"rekindled");
		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
			ResourceLoader.registerBuiltinPack(rekindled, container, PackActivationType.DEFAULT_ENABLED);
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
							LOGGER.info("[BigWater] Parsing data...");
							for (String key : settings.keySet()){
								int value = settings.get(key).getAsInt();
								LOGGER.info("-> " + key + ": " + value);
								textureScales.put(key, new Tuple<>(value, 1.0f/value));
							}
							LOGGER.info("Read resource pack provided settings");

						} catch(Exception e) {
							LOGGER.error("[BigWater] Failed to read resource pack settings");
							LOGGER.error(String.valueOf(e));
						}
					}
				}
		);

		LOGGER.info("[BigWater] Initialized");
	}

	public static Tuple<Integer, Float> getTextureScale(String identifier){
		if (textureScales.containsKey(identifier)){
			return textureScales.get(identifier);
		}
		return new Tuple<>(defaultTextureScale, defaultScalant);
	}

	public static void setConfig(String key, String value){
		CONFIG.set(key, value);

		if(key.equals(VAR_DEFAULTSCALE)){
			defaultTextureScale = CONFIG.getOrDefault(key, 1);
			defaultScalant = 1.0f/defaultTextureScale;
		}
	}

	public static void writeConfig(){
		CONFIG.writeToFile();
	}

	private static String provider( String filename ) {
		return "# Default scale for textures if resourcepacks don't provide any:\n"
				+ VAR_DEFAULTSCALE + "=1";
	}
}