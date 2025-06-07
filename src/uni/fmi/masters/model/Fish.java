package uni.fmi.masters.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Fish implements Serializable {
    private String name;
    private String waterType;
    private String preferredTemperature;
    private String aggressionLevel;
    private List<String> suitedForTankSizes;
    private List<String> eats;
    private List<String> compatibleWith;
    private List<String> sometimesCompatibleWith;
    private List<String> incompatibleWith;
    private List<String> suitableWithPlants;

    public Fish() {
    }

    public Fish(String name, String waterType, String preferredTemperature, String aggressionLevel,
            List<String> suitedForTankSizes, List<String> eats, List<String> compatibleWith,
            List<String> sometimesCompatibleWith, List<String> incompatibleWith, List<String> suitableWithPlants) {
        this.name = name;
        this.waterType = waterType;
        this.preferredTemperature = preferredTemperature;
        this.aggressionLevel = aggressionLevel;
        this.suitedForTankSizes = suitedForTankSizes;
        this.eats = eats;
        this.compatibleWith = compatibleWith;
        this.sometimesCompatibleWith = sometimesCompatibleWith;
        this.incompatibleWith = incompatibleWith;
        this.suitableWithPlants = suitableWithPlants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public String getPreferredTemperature() {
        return preferredTemperature;
    }

    public void setPreferredTemperature(String preferredTemperature) {
        this.preferredTemperature = preferredTemperature;
    }

    public String getAggressionLevel() {
        return aggressionLevel;
    }

    public void setAggressionLevel(String aggressionLevel) {
        this.aggressionLevel = aggressionLevel;
    }

    public List<String> getSuitedForTankSizes() {
        return suitedForTankSizes;
    }

    public void setSuitedForTankSizes(List<String> suitedForTankSizes) {
        this.suitedForTankSizes = suitedForTankSizes;
    }

    public List<String> getEats() {
        return eats;
    }

    public void setEats(List<String> eats) {
        this.eats = eats;
    }

    public List<String> getCompatibleWith() {
        return compatibleWith;
    }

    public void setCompatibleWith(List<String> compatibleWith) {
        this.compatibleWith = compatibleWith;
    }

    public List<String> getSometimesCompatibleWith() {
        return sometimesCompatibleWith;
    }

    public void setSometimesCompatibleWith(List<String> sometimesCompatibleWith) {
        this.sometimesCompatibleWith = sometimesCompatibleWith;
    }

    public List<String> getIncompatibleWith() {
        return incompatibleWith;
    }

    public void setIncompatibleWith(List<String> incompatibleWith) {
        this.incompatibleWith = incompatibleWith;
    }

    public List<String> getSuitableWithPlants() {
        return suitableWithPlants;
    }

    public void setSuitableWithPlants(List<String> suitableWithPlants) {
        this.suitableWithPlants = suitableWithPlants;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Fish: ").append(name).append(" ---\n");
        sb.append("  Water Type: ").append(waterType).append("\n");
        sb.append("  Temperature: ").append(preferredTemperature).append("\n");
        sb.append("  Aggression: ").append(aggressionLevel).append("\n");
        sb.append("  Suited Tank Sizes: ").append(suitedForTankSizes).append("\n");
        sb.append("  Eats: ").append(eats).append("\n");
        sb.append("  Compatible With: ").append(compatibleWith).append("\n");
        sb.append("  Sometimes Compatible With: ").append(sometimesCompatibleWith).append("\n");
        sb.append("  Incompatible With: ").append(incompatibleWith).append("\n");
        sb.append("  Suitable With Plants: ").append(suitableWithPlants).append("\n");
        return sb.toString();
    }
}