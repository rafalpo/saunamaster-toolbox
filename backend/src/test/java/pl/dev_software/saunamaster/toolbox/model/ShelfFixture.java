package pl.dev_software.saunamaster.toolbox.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShelfFixture {

    public static Shelf aShelf() {
        Shelf aShelf = new Shelf();
        aShelf.setName("Standardowa Półka");
        return aShelf;
    }

    public static Collection<Shelf> aShelves(int count) {
        Set<Shelf> shelves = new HashSet<>();
        for (int i = 0; i < count; i++) {
            shelves.add(aShelfWithRandomName());
        }
        return shelves;
    }

    public static Shelf aShelfWithRandomName() {
        String name = UUID.randomUUID().toString();
        Shelf aShelf = new Shelf();
        aShelf.setName(name);
        return aShelf;
    }

    public static Shelf aShelfWithName(String name) {
        Shelf aShelf = new Shelf();
        aShelf.setName(name);
        return aShelf;
    }

    public static Shelf aShelfWithItems(int itemsNumber) {
        Shelf aShelf = new Shelf();
        aShelf.setName("Standardowa Półka");
        Set<Item> items = new HashSet<>();
        for (int i = 0; i < itemsNumber; i++) {
            items.add(ItemFixture.anItemWithRandomName());
        }
        aShelf.setItems(items);
        return aShelf;
    }

}
