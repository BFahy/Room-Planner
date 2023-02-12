package Room_Planner;

public class FurnitureData implements Comparable {

    private String furniture;
    private int count;

    public FurnitureData(String furniture, int count) {
        this.furniture = furniture;
        this.count = count;
    }

    public String getFurniture() {
        return furniture;
    }

    public void setFurniture(String furniture) {
        this.furniture = furniture;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrementCount()
    {
        count++;
    }

    @Override
    public int compareTo(Object o) {
        FurnitureData other = (FurnitureData) o;
        return furniture.compareToIgnoreCase(other.getFurniture());
    }
}
