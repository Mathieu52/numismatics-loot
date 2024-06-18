package net.xzera.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import io.wispforest.lavender.book.Book;
import io.wispforest.lavender.book.Category;
import io.wispforest.lavender.book.Entry;

@Mixin(Book.class)
public interface BookInvoker {
    @Invoker(value = "addEntry", remap = false)
    public void addEntryInvoker(Entry entry);

    @Invoker(value = "addCategory", remap = false)
    public void addCategoryInvoker(Category entry);
}
