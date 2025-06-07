
package uni.fmi.masters.model;

public class Plant {
    private String name;
    private String tankSize;
    private String amount;

    public Plant() {
    }

    public Plant(String name) {
        this.name = name;
    }

    public Plant(String name, String tankSize, String amount) {
        this.name = name;
        this.tankSize = tankSize;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTankSize() {
        return tankSize;
    }

    public void setTankSize(String tankSize) {
        this.tankSize = tankSize;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "--- Plant: " + name + " (Recommended Amount: " + amount + ") ---\n";
    }
}