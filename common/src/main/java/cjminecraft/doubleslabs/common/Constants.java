package cjminecraft.doubleslabs.common;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "doubleslabs";
	public static final String MOD_NAME = "DoubleSlabs";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

}