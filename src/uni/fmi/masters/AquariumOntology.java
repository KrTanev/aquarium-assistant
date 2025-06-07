package uni.fmi.masters;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import uni.fmi.masters.model.Fish;
import uni.fmi.masters.model.Plant;

public class AquariumOntology {

    private OWLOntologyManager ontoManager;
    private OWLOntology aquariumOntology;
    private OWLDataFactory dataFactory;

    private String ontologyIRIStr;

    public AquariumOntology() {
        ontoManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontoManager.getOWLDataFactory();
        loadOntology();

        Optional<IRI> optionalIRI = Optional.ofNullable(aquariumOntology.getOntologyID().getOntologyIRI());
        ontologyIRIStr = optionalIRI.map(IRI::toString)
                .orElseThrow(() -> new IllegalStateException("Ontology IRI not found")) + "#";
    }

    private void loadOntology() {
        File ontologyFile = new File("src/files/aquarium.owl");

        try {
            if (!ontologyFile.exists()) {
                System.out.println("Ontology file not found, creating a new empty ontology.");
                IRI baseIRI = IRI.create("http://www.fmi.uni.bg/masters/aquarium-advisor-ontology");
                aquariumOntology = ontoManager.createOntology(baseIRI);
                ontologyIRIStr = baseIRI.toString() + "#";
                initializeOntologySchema();
                saveOntology();
            } else {
                aquariumOntology = ontoManager.loadOntologyFromOntologyDocument(ontologyFile);
                System.out.println(
                        "Aquarium ontology loaded successfully: " + aquariumOntology.getOntologyID().getOntologyIRI());
                Optional<IRI> optionalIRI = Optional.ofNullable(aquariumOntology.getOntologyID().getOntologyIRI());
                ontologyIRIStr = optionalIRI.map(IRI::toString)
                        .orElseThrow(() -> new IllegalStateException("Ontology IRI not found")) + "#";
            }
        } catch (OWLOntologyCreationException e) {
            System.err.println("Error loading or creating aquarium ontology: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeOntologySchema() {
        addAxiom(dataFactory.getOWLDeclarationAxiom(dataFactory.getOWLClass(IRI.create(ontologyIRIStr
                + "Fish"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(dataFactory.getOWLClass(IRI.create(ontologyIRIStr
                + "Plant"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(dataFactory.getOWLClass(IRI.create(ontologyIRIStr
                + "WaterType"))));
        addAxiom(dataFactory
                .getOWLDeclarationAxiom(dataFactory.getOWLClass(IRI.create(ontologyIRIStr +
                        "TankSizeRange"))));
        addAxiom(dataFactory
                .getOWLDeclarationAxiom(dataFactory.getOWLClass(IRI.create(ontologyIRIStr +
                        "WaterTemperature"))));
        addAxiom(dataFactory
                .getOWLDeclarationAxiom(dataFactory.getOWLClass(IRI.create(ontologyIRIStr +
                        "AggressionLevel"))));

        // Data Properties
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr +
                        "hasTemperatureRange"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr +
                        "hasAggressionLevel"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr +
                        "hasMinimumTankSize"))));
        addAxiom(dataFactory
                .getOWLDeclarationAxiom(dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr
                        + "hasWaterType"))));
        addAxiom(dataFactory
                .getOWLDeclarationAxiom(dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr
                        + "hasEats"))));
        addAxiom(dataFactory
                .getOWLDeclarationAxiom(dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr
                        + "plantAmount"))));

        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr +
                        "isCompatibleWith"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr +
                        "sometimesCompatibleWith"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr +
                        "incompatibleWith"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr +
                        "suitedForTankSize"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr +
                        "suitableWithPlant"))));
        addAxiom(dataFactory
                .getOWLDeclarationAxiom(dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr
                        + "eats"))));
        addAxiom(dataFactory.getOWLDeclarationAxiom(
                dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr +
                        "suitablePlantsForTankSize"))));
    }

    public void saveOntology() {
        try {
            File ontologyFile = new File("src/files/aquarium.owl");
            ontoManager.saveOntology(aquariumOntology, new FileOutputStream(ontologyFile));
            System.out.println("Ontology saved to: " + ontologyFile.getAbsolutePath());
        } catch (OWLOntologyStorageException | FileNotFoundException e) {
            System.err.println("Failed to save ontology: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFriendlyName(IRI iri) {
        String fullIRI = iri.toString();

        int hashIndex = fullIRI.lastIndexOf("#");
        if (hashIndex != -1) {
            return fullIRI.substring(hashIndex + 1);
        }

        int slashIndex = fullIRI.lastIndexOf("/");
        if (slashIndex != -1) {
            return fullIRI.substring(slashIndex + 1);
        }
        return fullIRI;
    }

    private String getFriendlyName(OWLNamedIndividual individual) {
        return getFriendlyName(individual.getIRI());
    }

    /**
     * Helper method to get explicitly asserted object property values for a given
     * individual and property.
     * This replaces the incorrect `aquariumOntology.getObjectPropertyValues()`
     * calls.
     *
     * @param subject  The individual whose property values are sought.
     * @param property The object property.
     * @return A Set of OWLNamedIndividual objects that are asserted as values of
     *         the property for the subject.
     */
    private Set<OWLNamedIndividual> getAssertedObjectPropertyValues(
            OWLNamedIndividual subject, OWLObjectProperty property) {
        return aquariumOntology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION).stream()
                .filter(ax -> ax.getSubject().equals(subject) && ax.getProperty().equals(property))
                .map(OWLObjectPropertyAssertionAxiom::getObject)
                .filter(OWLIndividual::isNamed)
                .map(ind -> (OWLNamedIndividual) ind)
                .collect(Collectors.toSet());
    }

    /**
     * Helper method to get explicitly asserted data property values for a given
     * individual and data property.
     * This replaces the incorrect `aquariumOntology.getDataPropertyAssertions()`
     * calls for retrieving values.
     *
     * @param subject  The individual whose data property values are sought.
     * @param property The data property.
     * @return A Set of String literals that are asserted as values of the data
     *         property for the subject.
     */
    private Set<String> getAssertedDataPropertyValues(
            OWLNamedIndividual subject, OWLDataProperty property) {
        return aquariumOntology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION).stream()
                .filter(ax -> ax.getSubject().equals(subject) && ax.getProperty().equals(property))
                .map(OWLDataPropertyAssertionAxiom::getObject)
                .map(literal -> literal.getLiteral())
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves suitable fish based on tank size, water type, and preferred
     * temperature.
     *
     * @param tankSize             e.g., "L50to100"
     * @param waterType            e.g., "Freshwater"
     * @param preferredTemperature e.g., "Warm"
     * @return List of suitable Fish objects.
     */
    public List<Fish> getSuitableFish(String tankSize, String waterType, String preferredTemperature) {
        List<Fish> suitableFish = new ArrayList<>();

        OWLClass fishClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "Fish"));
        OWLNamedIndividual tankSizeInd = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + tankSize));
        OWLNamedIndividual waterTypeInd = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + waterType));
        OWLNamedIndividual tempInd = dataFactory
                .getOWLNamedIndividual(IRI.create(ontologyIRIStr + preferredTemperature));

        OWLObjectProperty suitedForTankSizeProp = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "suitedForTankSize"));
        OWLObjectProperty hasWaterTypeProp = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasWaterType"));
        OWLObjectProperty preferredTemperatureProp = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "preferredTemperature"));

        Set<OWLNamedIndividual> allFishIndividuals = aquariumOntology.getIndividualsInSignature().stream()
                .filter(ind -> aquariumOntology.getAxioms(AxiomType.CLASS_ASSERTION)
                        .stream()
                        .anyMatch(ca -> ca.getIndividual().equals(ind) && ca.getClassExpression().equals(fishClass)))
                .collect(Collectors.toSet());

        for (OWLNamedIndividual fishIndividual : allFishIndividuals) {
            boolean isSuitable = true;

            Set<OWLNamedIndividual> suitedTankSizes = getAssertedObjectPropertyValues(fishIndividual,
                    suitedForTankSizeProp);
            if (!suitedTankSizes.contains(tankSizeInd)) {
                isSuitable = false;
            }

            Set<OWLNamedIndividual> fishWaterTypes = getAssertedObjectPropertyValues(fishIndividual, hasWaterTypeProp);
            if (!fishWaterTypes.contains(waterTypeInd)) {
                isSuitable = false;
            }

            Set<OWLNamedIndividual> fishTemperatures = getAssertedObjectPropertyValues(fishIndividual,
                    preferredTemperatureProp);
            if (!fishTemperatures.contains(tempInd)) {
                isSuitable = false;
            }

            if (isSuitable) {
                Fish fish = new Fish();
                fish.setName(getFriendlyName(fishIndividual));

                fish.setWaterType(getFriendlyName(waterTypeInd));
                fish.setPreferredTemperature(getFriendlyName(tempInd));

                OWLObjectProperty hasAggressionLevelProp = dataFactory
                        .getOWLObjectProperty(IRI.create(ontologyIRIStr + "hasAggressionLevel"));
                fish.setAggressionLevel(getAssertedObjectPropertyValues(fishIndividual, hasAggressionLevelProp)
                        .stream().findFirst().map(this::getFriendlyName).orElse("Unknown"));

                fish.setSuitedForTankSizes(getAssertedObjectPropertyValues(fishIndividual, suitedForTankSizeProp)
                        .stream().map(this::getFriendlyName).collect(Collectors.toList()));

                OWLObjectProperty eatsProp = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRIStr + "eats"));
                fish.setEats(getAssertedObjectPropertyValues(fishIndividual, eatsProp)
                        .stream().map(this::getFriendlyName).collect(Collectors.toList()));

                OWLObjectProperty isCompatibleWithProp = dataFactory
                        .getOWLObjectProperty(IRI.create(ontologyIRIStr + "isCompatibleWith"));
                fish.setCompatibleWith(getAssertedObjectPropertyValues(fishIndividual, isCompatibleWithProp)
                        .stream().map(this::getFriendlyName).collect(Collectors.toList()));

                OWLObjectProperty sometimesCompatibleWithProp = dataFactory
                        .getOWLObjectProperty(IRI.create(ontologyIRIStr + "sometimesCompatibleWith"));
                fish.setSometimesCompatibleWith(
                        getAssertedObjectPropertyValues(fishIndividual, sometimesCompatibleWithProp)
                                .stream().map(this::getFriendlyName).collect(Collectors.toList()));

                OWLObjectProperty incompatibleWithProp = dataFactory
                        .getOWLObjectProperty(IRI.create(ontologyIRIStr + "incompatibleWith"));
                fish.setIncompatibleWith(getAssertedObjectPropertyValues(fishIndividual, incompatibleWithProp)
                        .stream().map(this::getFriendlyName).collect(Collectors.toList()));

                OWLObjectProperty suitableWithPlantProp = dataFactory
                        .getOWLObjectProperty(IRI.create(ontologyIRIStr + "suitableWithPlant"));
                fish.setSuitableWithPlants(getAssertedObjectPropertyValues(fishIndividual, suitableWithPlantProp)
                        .stream().map(this::getFriendlyName).collect(Collectors.toList()));

                suitableFish.add(fish);
            }
        }

        return suitableFish;
    }

    /**
     * Retrieves suitable plants for a given tank size.
     *
     * @param tankSize e.g., "L50to100"
     * @return List of suitable Plant objects.
     */
    public List<Plant> getSuitablePlantsForTankSize(String tankSize) {
        List<Plant> suitablePlants = new ArrayList<>();

        OWLClass tankSizeRangeClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "TankSizeRange"));
        OWLNamedIndividual tankSizeInd = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + tankSize));

        OWLObjectProperty suitablePlantsForTankSizeProp = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "suitablePlantsForTankSize"));
        OWLDataProperty plantAmountProp = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "plantAmount"));

        if (!aquariumOntology.getAxioms(AxiomType.CLASS_ASSERTION)
                .stream()
                .anyMatch(ca -> ca.getIndividual().equals(tankSizeInd)
                        && ca.getClassExpression().equals(tankSizeRangeClass))) {
            System.out.println("Warning: " + tankSize + " is not an individual of TankSizeRange or does not exist.");
            return suitablePlants;
        }

        Set<OWLNamedIndividual> plantsForTank = getAssertedObjectPropertyValues(tankSizeInd,
                suitablePlantsForTankSizeProp);

        String amount = getAssertedDataPropertyValues(tankSizeInd, plantAmountProp)
                .stream()
                .findFirst()
                .orElse("N/A");

        for (OWLNamedIndividual plantIndividual : plantsForTank) {
            Plant plant = new Plant(getFriendlyName(plantIndividual), getFriendlyName(tankSizeInd), amount);
            suitablePlants.add(plant);
        }

        return suitablePlants;
    }

    /**
     * Checks compatibility between two fish.
     * 
     * @param fish1Name Name of the first fish (e.g., "Guppy")
     * @param fish2Name Name of the second fish (e.g., "Molly")
     * @return A string indicating compatibility: "Compatible", "Sometimes
     *         Compatible", "Incompatible", or "Unknown".
     */
    public String checkFishCompatibility(String fish1Name, String fish2Name) {
        OWLNamedIndividual fish1 = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + fish1Name));
        OWLNamedIndividual fish2 = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRIStr + fish2Name));

        OWLObjectProperty isCompatibleWith = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "isCompatibleWith"));
        OWLObjectProperty sometimesCompatibleWith = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "sometimesCompatibleWith"));
        OWLObjectProperty incompatibleWith = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "incompatibleWith"));

        if (getAssertedObjectPropertyValues(fish1, isCompatibleWith).contains(fish2)) {
            return "Compatible";
        }
        if (getAssertedObjectPropertyValues(fish1, sometimesCompatibleWith).contains(fish2)) {
            return "Sometimes Compatible";
        }
        if (getAssertedObjectPropertyValues(fish1, incompatibleWith).contains(fish2)) {
            return "Incompatible";
        }

        if (getAssertedObjectPropertyValues(fish2, isCompatibleWith).contains(fish1)) {
            return "Compatible";
        }
        if (getAssertedObjectPropertyValues(fish2, sometimesCompatibleWith).contains(fish1)) {
            return "Sometimes Compatible";
        }
        if (getAssertedObjectPropertyValues(fish2, incompatibleWith).contains(fish1)) {
            return "Incompatible";
        }

        return "Unknown";
    }

    /**
     * Retrieves all known tank size ranges from the ontology.
     * 
     * @return A list of strings representing tank size ranges (e.g., "Below20L",
     *         "L20to50").
     */
    public List<String> getAllTankSizeRanges() {
        OWLClass tankSizeRangeClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "TankSizeRange"));
        return aquariumOntology.getIndividualsInSignature().stream()
                .filter(ind -> aquariumOntology.getAxioms(AxiomType.CLASS_ASSERTION)
                        .stream()
                        .anyMatch(ca -> ca.getIndividual().equals(ind)
                                && ca.getClassExpression().equals(tankSizeRangeClass)))
                .map(this::getFriendlyName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all known water types from the ontology.
     * 
     * @return A list of strings representing water types (e.g., "Freshwater",
     *         "Saltwater").
     */
    public List<String> getAllWaterTypes() {
        OWLClass waterTypeClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "WaterType"));
        return aquariumOntology.getIndividualsInSignature().stream()
                .filter(ind -> aquariumOntology.getAxioms(AxiomType.CLASS_ASSERTION)
                        .stream()
                        .anyMatch(
                                ca -> ca.getIndividual().equals(ind) && ca.getClassExpression().equals(waterTypeClass)))
                .map(this::getFriendlyName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all known water temperatures from the ontology.
     * 
     * @return A list of strings representing water temperatures (e.g., "Warm",
     *         "Cold").
     */
    public List<String> getAllWaterTemperatures() {
        OWLClass waterTemperatureClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "WaterTemperature"));
        return aquariumOntology.getIndividualsInSignature().stream()
                .filter(ind -> aquariumOntology.getAxioms(AxiomType.CLASS_ASSERTION)
                        .stream()
                        .anyMatch(ca -> ca.getIndividual().equals(ind)
                                && ca.getClassExpression().equals(waterTemperatureClass)))
                .map(this::getFriendlyName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all known aggression levels from the ontology.
     * 
     * @return A list of strings representing aggression levels (e.g., "Peaceful",
     *         "Aggressive").
     */
    public List<String> getAllAggressionLevels() {
        OWLClass aggressionLevelClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "AggressionLevel"));
        return aquariumOntology.getIndividualsInSignature().stream()
                .filter(ind -> aquariumOntology.getAxioms(AxiomType.CLASS_ASSERTION)
                        .stream()
                        .anyMatch(ca -> ca.getIndividual().equals(ind)
                                && ca.getClassExpression().equals(aggressionLevelClass)))
                .map(this::getFriendlyName)
                .collect(Collectors.toList());
    }

    public boolean createNewFish(String name, String temperature, String aggression, String tankSize,
            String waterType) {
        IRI fishIRI = IRI.create(ontologyIRIStr + "#" + name.replace(" ", "_"));
        OWLNamedIndividual fish = dataFactory.getOWLNamedIndividual(fishIRI);

        if (aquariumOntology.containsIndividualInSignature(fishIRI)) {
            return false;
        }

        OWLClass fishClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "#Fish"));
        addAxiom(dataFactory.getOWLClassAssertionAxiom(fishClass, fish));

        addDataProperty(fish, "hasTemperatureRange", temperature);
        addDataProperty(fish, "hasAggressionLevel", aggression);
        addDataProperty(fish, "hasMinimumTankSize", tankSize);
        addDataProperty(fish, "hasWaterType", waterType);

        return true;
    }

    public boolean createNewPlant(String name, String temperature, String tankSize, String waterType) {
        IRI plantIRI = IRI.create(ontologyIRIStr + "#" + name.replace(" ", "_"));
        OWLNamedIndividual plant = dataFactory.getOWLNamedIndividual(plantIRI);

        if (aquariumOntology.containsIndividualInSignature(plantIRI)) {
            return false;
        }

        OWLClass plantClass = dataFactory.getOWLClass(IRI.create(ontologyIRIStr + "#Plant"));
        addAxiom(dataFactory.getOWLClassAssertionAxiom(plantClass, plant));

        addDataProperty(plant, "hasTemperatureRange", temperature);
        addDataProperty(plant, "hasMinimumTankSize", tankSize);
        addDataProperty(plant, "hasWaterType", waterType);

        return true;
    }

    public boolean setFishCompatibility(String fishName1, String fishName2) {
        IRI fishIRI1 = IRI.create(ontologyIRIStr + "#" + fishName1.replace(" ", "_"));
        IRI fishIRI2 = IRI.create(ontologyIRIStr + "#" + fishName2.replace(" ", "_"));

        OWLNamedIndividual fish1 = dataFactory.getOWLNamedIndividual(fishIRI1);
        OWLNamedIndividual fish2 = dataFactory.getOWLNamedIndividual(fishIRI2);

        if (!aquariumOntology.containsIndividualInSignature(fishIRI1)
                || !aquariumOntology.containsIndividualInSignature(fishIRI2)) {
            return false;
        }

        OWLObjectProperty compatibleWith = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "#isCompatibleWith"));
        addAxiom(dataFactory.getOWLObjectPropertyAssertionAxiom(compatibleWith, fish1, fish2));
        addAxiom(dataFactory.getOWLObjectPropertyAssertionAxiom(compatibleWith, fish2, fish1));

        return true;
    }

    public boolean setFishSometimesCompatibility(String fishName1, String fishName2) {
        IRI fishIRI1 = IRI.create(ontologyIRIStr + fishName1.replace(" ", "_"));
        IRI fishIRI2 = IRI.create(ontologyIRIStr + fishName2.replace(" ", "_"));

        OWLNamedIndividual fish1 = dataFactory.getOWLNamedIndividual(fishIRI1);
        OWLNamedIndividual fish2 = dataFactory.getOWLNamedIndividual(fishIRI2);

        if (!aquariumOntology.containsIndividualInSignature(fishIRI1)
                || !aquariumOntology.containsIndividualInSignature(fishIRI2)) {
            System.err.println("One or both fish individuals not found for sometimes compatibility: " + fishName1 + ", "
                    + fishName2);
            return false;
        }

        OWLObjectProperty sometimesCompatibleWith = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "sometimesCompatibleWith"));
        addAxiom(dataFactory.getOWLObjectPropertyAssertionAxiom(sometimesCompatibleWith, fish1, fish2));
        addAxiom(dataFactory.getOWLObjectPropertyAssertionAxiom(sometimesCompatibleWith, fish2, fish1));

        System.out.println("Set sometimes compatibility between: " + fishName1 + " and " + fishName2);
        return true;
    }

    public boolean setFishIncompatibility(String fishName1, String fishName2) {
        IRI fishIRI1 = IRI.create(ontologyIRIStr + fishName1.replace(" ", "_"));
        IRI fishIRI2 = IRI.create(ontologyIRIStr + fishName2.replace(" ", "_"));

        OWLNamedIndividual fish1 = dataFactory.getOWLNamedIndividual(fishIRI1);
        OWLNamedIndividual fish2 = dataFactory.getOWLNamedIndividual(fishIRI2);

        if (!aquariumOntology.containsIndividualInSignature(fishIRI1)
                || !aquariumOntology.containsIndividualInSignature(fishIRI2)) {
            System.err.println(
                    "One or both fish individuals not found for incompatibility: " + fishName1 + ", " + fishName2);
            return false;
        }

        OWLObjectProperty incompatibleWith = dataFactory
                .getOWLObjectProperty(IRI.create(ontologyIRIStr + "incompatibleWith"));
        addAxiom(dataFactory.getOWLObjectPropertyAssertionAxiom(incompatibleWith, fish1, fish2));
        addAxiom(dataFactory.getOWLObjectPropertyAssertionAxiom(incompatibleWith, fish2, fish1));

        System.out.println("Set incompatibility between: " + fishName1 + " and " + fishName2);
        return true;
    }

    public boolean setFishEats(String fishName, List<String> foodItems) {
        IRI fishIRI = IRI.create(ontologyIRIStr + fishName.replace(" ", "_"));
        OWLNamedIndividual fish = dataFactory.getOWLNamedIndividual(fishIRI);

        if (!aquariumOntology.containsIndividualInSignature(fishIRI)) {
            System.err.println("Fish individual not found for setting eats: " + fishName);
            return false;
        }

        OWLDataProperty eatsProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "hasEats"));

        Set<OWLAxiom> axiomsToRemove = aquariumOntology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION).stream()
                .filter(ax -> ax.getSubject().equals(fish) && ax.getProperty().equals(eatsProperty))
                .collect(Collectors.toSet());
        ontoManager.removeAxioms(aquariumOntology, axiomsToRemove);

        for (String food : foodItems) {
            addDataProperty(fish, "hasEats", food);
        }
        System.out.println("Set eats for " + fishName + ": " + String.join(", ", foodItems));
        return true;
    }

    public boolean setPlantAmount(String plantName, int amount) {
        IRI plantIRI = IRI.create(ontologyIRIStr + plantName.replace(" ", "_"));
        OWLNamedIndividual plant = dataFactory.getOWLNamedIndividual(plantIRI);

        if (!aquariumOntology.containsIndividualInSignature(plantIRI)) {
            System.err.println("Plant individual not found for setting amount: " + plantName);
            return false;
        }

        OWLDataProperty amountProperty = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + "plantAmount"));

        Set<OWLAxiom> axiomsToRemove = aquariumOntology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION).stream()
                .filter(ax -> ax.getSubject().equals(plant) && ax.getProperty().equals(amountProperty))
                .collect(Collectors.toSet());
        ontoManager.removeAxioms(aquariumOntology, axiomsToRemove);

        addDataProperty(plant, "plantAmount", String.valueOf(amount));
        System.out.println("Set amount for " + plantName + ": " + amount);
        return true;
    }

    // Helper for adding data properties
    private void addDataProperty(OWLNamedIndividual individual, String propertyName, String value) {
        OWLDataProperty property = dataFactory.getOWLDataProperty(IRI.create(ontologyIRIStr + propertyName));
        OWLDataPropertyAssertionAxiom axiom = dataFactory.getOWLDataPropertyAssertionAxiom(
                property,
                individual,
                dataFactory.getOWLLiteral(value, OWL2Datatype.XSD_STRING));
        ontoManager.addAxiom(aquariumOntology, axiom);
    }

    // Helper for adding axioms without immediate save
    private void addAxiom(OWLAxiom axiom) {
        ontoManager.addAxiom(aquariumOntology, axiom);
    }
}