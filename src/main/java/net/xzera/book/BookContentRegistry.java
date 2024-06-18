package net.xzera.book;

import io.wispforest.lavender.book.*;
import net.minecraft.resources.ResourceLocation;
import net.xzera.mixin.BookInvoker;

import java.util.*;

public class BookContentRegistry {
    private static final HashMap<ResourceLocation, Set<Entry>> entries = new HashMap<>();
    private static final HashMap<ResourceLocation, Set<Category>> categories = new HashMap<>();

    public static void reloadContent() {
        for (Book book : BookLoader.loadedBooks()) {
            Set<Category> categories = BookContentRegistry.categories.get(book.id());
            if (categories == null)
                continue;

            for (Category category : categories) {
                ((BookInvoker) (Object) book).addCategoryInvoker(category);
            }
        }

        for (Book book : BookLoader.loadedBooks()) {
            Set<Entry> entries = BookContentRegistry.entries.get(book.id());
            if (entries == null)
                continue;

            for (Entry entry : entries) {
                ((BookInvoker) (Object) book).addEntryInvoker(entry);
            }
        }
    }

    public static void addEntry(ResourceLocation bookIdentifier, Entry entry) {
        if (entries.containsKey(bookIdentifier)) {
            Set<Entry> entries = BookContentRegistry.entries.get(bookIdentifier);
            if(!entries.add(entry)) {
                entries.remove(entry);
                entries.add(entry);
            }
        } else {
            Set<Entry> newEntry = new HashSet<>();
            newEntry.add(entry);
            entries.putIfAbsent(bookIdentifier, newEntry);
        }

        Book book = BookLoader.get(bookIdentifier);
        if (book != null) {
            ((BookInvoker) (Object) book).addEntryInvoker(entry);
        }
    }

    public static void addCategory(ResourceLocation bookIdentifier, Category category) {
        if (categories.containsKey(bookIdentifier)) {
            Set<Category> categories = BookContentRegistry.categories.get(bookIdentifier);
            if(!categories.add(category)) {
                categories.remove(category);
                categories.add(category);
            }
        } else {
            Set<Category> newEntry = new HashSet<>();
            newEntry.add(category);
            categories.putIfAbsent(bookIdentifier, newEntry);
        }

        Book book = BookLoader.get(bookIdentifier);
        if (book != null) {
            ((BookInvoker) (Object) book).addCategoryInvoker(category);
        }
    }

    public static void clear() {
        entries.clear();
        categories.clear();
    }
}
