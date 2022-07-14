package KnapsackClasses;

import java.util.List;

public class Knapsack {
    private int id; //id is negative for 0/1 problems
    private int itemsCount;
    private int capacity;
    private int minPrice;

    private List<Item> items;

    public Knapsack(int id, int itemsCount, int capacity, int minPrice, List<Item> items) {
        this.id = id;
        this.itemsCount = itemsCount;
        this.capacity = capacity;
        this.minPrice = minPrice;
        this.items = items;
    }

    @Override
    public String toString() {
        return "Knapsack{" +
                "id=" + id +
                ", itemsCount=" + itemsCount +
                ", capacity=" + capacity +
                ", minPrice=" + minPrice +
                ", items=" + items +
                '}';
    }

    public void sortItemsByPrice() {
        items.sort(
                (a, b) -> {
                    if (a.getPrice() > b.getPrice()) {
                        return 1;
                    }
                    if (a.getPrice() < b.getPrice()) {
                        return -1;
                    }
                    return a.getWeight() - b.getWeight();
                }
        );
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
