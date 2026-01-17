package pl.dev_software.saunamaster.toolbox.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemFixture {

    public static Item anItem() {
        Item anItem = new Item();
        anItem.setName("Standardowy Item");
        return anItem;
    }

    public static Item anItemWithRandomName() {
        String name = UUID.randomUUID().toString();
        Item anItem = new Item();
        anItem.setName(name);
        return anItem;
    }

    public static Item anItemWithName(String name) {
        Item anItem = new Item();
        anItem.setName(name);
        return anItem;
    }

    public static Collection<Item> anItemsOnShelf(Shelf shelf, int totalItems) {
        Set<Item> items = new HashSet<>();
        for (int i = 0; i < totalItems; i++) {
            Item item = anItemWithRandomName();
            item.setShelf(shelf);
            items.add(item);
        }
        return items;
    }
}
