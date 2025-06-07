package uni.fmi.masters.model;

import java.util.ArrayList;
import java.util.List;

public class Fish {
    private String name;
    private String waterType;
    private String preferredTemperature;
    private String aggressionLevel;

    private List<String> suitedForTankSizes = new ArrayList<>();
    private List<String> eats = new ArrayList<>();
    private List<String> compatibleWith = new ArrayList<>();
    private List<String> sometimesCompatibleWith = new ArrayList<>();
    private List<String> incompatibleWith = new ArrayList<>();
    private List<String> suitableWithPlants = new ArrayList<>();

    public Fish() {
    }

    public Fish(String name, String waterType, String preferredTemperature, String aggressionLevel) {
        this.name = name;
        this.waterType = waterType;
        this.preferredTemperature = preferredTemperature;
        this.aggressionLevel = aggressionLevel;
    }

    public String getName() {
        return name;
    }

    public String getWaterType() {
        return waterType;
    }

    public String getPreferredTemperature() {
        return preferredTemperature;
    }

    public String getAggressionLevel() {
        return aggressionLevel;
    }

    public List<String> getSuitedForTankSizes() {
        return suitedForTankSizes;
    }

    public List<String> getEats() {
        return eats;
    }

    public List<String> getCompatibleWith() {
        return compatibleWith;
    }

    public List<String> getSometimesCompatibleWith() {
        return sometimesCompatibleWith;
    }

    public List<String> getIncompatibleWith() {
        return incompatibleWith;
    }

    public List<String> getSuitableWithPlants() {
        return suitableWithPlants;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public void setPreferredTemperature(String preferredTemperature) {
        this.preferredTemperature = preferredTemperature;
    }

    public void setAggressionLevel(String aggressionLevel) {
        this.aggressionLevel = aggressionLevel;
    }

    public void setSuitedForTankSizes(List<String> suitedForTankSizes) {
        this.suitedForTankSizes = (suitedForTankSizes != null) ? suitedForTankSizes : new ArrayList<>();
    }

    public void setEats(List<String> eats) {
        this.eats = (eats != null) ? eats : new ArrayList<>();
    }

    public void setCompatibleWith(List<String> compatibleWith) {
        this.compatibleWith = (compatibleWith != null) ? compatibleWith : new ArrayList<>();
    }

    public void setSometimesCompatibleWith(List<String> sometimesCompatibleWith) {
        this.sometimesCompatibleWith = (sometimesCompatibleWith != null) ? sometimesCompatibleWith : new ArrayList<>();
    }

    public void setIncompatibleWith(List<String> incompatibleWith) {
        this.incompatibleWith = (incompatibleWith != null) ? incompatibleWith : new ArrayList<>();
    }

    public void setSuitableWithPlants(List<String> suitableWithPlants) {
        this.suitableWithPlants = (suitableWithPlants != null) ? suitableWithPlants : new ArrayList<>();
    }
}