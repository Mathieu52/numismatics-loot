package net.xzera.item;

import io.wispforest.lavender.book.LavenderBookItem;
import net.minecraft.resources.ResourceLocation;

import static net.xzera.NumismaticsLoot.MOD_ID;

public class NumismaticsEncyclopediaItem extends LavenderBookItem {
	public static final ResourceLocation BOOK_ID = new ResourceLocation(MOD_ID, "numismatics_encyclopedia");

	public static final NumismaticsEncyclopediaItem BOOK_ITEM = new NumismaticsEncyclopediaItem(new Properties().stacksTo(1));

	protected NumismaticsEncyclopediaItem(Properties settings) {
		super(settings, BOOK_ID);
	}
}
