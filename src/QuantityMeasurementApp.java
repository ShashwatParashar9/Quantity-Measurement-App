package com.apps.quantitymeasurement;

import java.util.Objects;

/**
 * UC10: Generic Quantity Class with Unit Interface
 * This code is standalone and requires NO external libraries (No JUnit).
 */

// --- STEP 1: Define IMeasurable Interface ---
/**
 * Standardizes unit behavior across all measurement categories.
 */
interface IMeasurable {
    /** @return the conversion factor relative to the base unit. */
    double getConversionFactor();

    /** @return value converted to the base unit. */
    double convertToBaseUnit(double value);

    /** @return value converted from the base unit to this unit. */
    double convertFromBaseUnit(double baseValue);

    /** @return the readable name of the unit. */
    String getUnitName();
}

// --- STEP 2: Refactor LengthUnit Enum ---
/**
 * Implements IMeasurable for length units.
 */
enum LengthUnit implements IMeasurable {
    FEET(12.0),
    INCHES(1.0),
    YARDS(36.0),
    CENTIMETERS(0.393701); // 1 cm = 0.393701 inches

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override public double getConversionFactor() { return conversionFactor; }
    @Override public double convertToBaseUnit(double value) { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double baseValue) { return baseValue / conversionFactor; }
    @Override public String getUnitName() { return this.name(); }
}

// --- STEP 3: Refactor WeightUnit Enum ---
/**
 * Implements IMeasurable for weight units.
 */
enum WeightUnit implements IMeasurable {
    GRAM(1.0),
    KILOGRAM(1000.0),
    TONNE(1000000.0);

    private final double conversionFactor;

    WeightUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override public double getConversionFactor() { return conversionFactor; }
    @Override public double convertToBaseUnit(double value) { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double baseValue) { return baseValue / conversionFactor; }
    @Override public String getUnitName() { return this.name(); }
}

// --- STEP 4: Generic Quantity Class ---
/**
 * A generic class that handles values for any unit implementing IMeasurable.
 * This eliminates the need for parallel QuantityLength and QuantityWeight classes.
 */
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    /**
     * Converts to a new target unit within the same category.
     */
    public Quantity<U> convertTo(U targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        // Round to 2 decimal places
        double roundedValue = Math.round(convertedValue * 100.0) / 100.0;
        return new Quantity<>(roundedValue, targetUnit);
    }

    /**
     * Adds two quantities together and returns the result in a target unit.
     */
    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double totalBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(totalBase), targetUnit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity<?> that = (Quantity<?>) o;

        // Prevents cross-category comparisons (e.g., Length vs Weight)
        if (this.unit.getClass() != that.unit.getClass()) return false;

        // Compare values in base unit using precision delta
        double thisBase = this.unit.convertToBaseUnit(this.value);
        double thatBase = ((IMeasurable)that.unit).convertToBaseUnit((Double)that.value);
        return Math.abs(thisBase - thatBase) < 0.001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.convertToBaseUnit(value), unit.getClass());
    }

    @Override
    public String toString() {
        return value + " " + unit.getUnitName();
    }
}

// --- STEP 5: Main Application ---
public class QuantityMeasurementApp {

    /**
     * Generic method to verify equality between quantities.
     */
    public static <U extends IMeasurable> void verifyEquality(Quantity<U> q1, Quantity<U> q2, String testName) {
        boolean isEqual = q1.equals(q2);
        System.out.println(testName + " | Comparing [" + q1 + "] and [" + q2 + "] -> " + (isEqual ? "PASS (Equal)" : "FAIL (Not Equal)"));
    }

    public static void main(String[] args) {
        System.out.println("=== UC10 Standalone Execution (No Dependencies) ===\n");

        // Length Category Tests
        System.out.println("--- Length Operations ---");
        Quantity<LengthUnit> oneFeet = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> twelveInches = new Quantity<>(12.0, LengthUnit.INCHES);
        verifyEquality(oneFeet, twelveInches, "UC1: Feet to Inches");

        Quantity<LengthUnit> oneYard = new Quantity<>(1.0, LengthUnit.YARDS);
        Quantity<LengthUnit> threeFeet = new Quantity<>(3.0, LengthUnit.FEET);
        verifyEquality(oneYard, threeFeet, "UC4: Yard to Feet");

        // Weight Category Tests
        System.out.println("\n--- Weight Operations ---");
        Quantity<WeightUnit> oneKg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> thousandGram = new Quantity<>(1000.0, WeightUnit.GRAM);
        verifyEquality(oneKg, thousandGram, "UC9: Kg to Gram");

        // Addition and Conversion
        System.out.println("\n--- Addition/Conversion ---");
        Quantity<LengthUnit> sum = oneFeet.add(twelveInches, LengthUnit.FEET);
        System.out.println("Result: 1 Feet + 12 Inches = " + sum); // Should be 2.0 FEET

        // Cross-Category Prevention
        System.out.println("\n--- Type Safety Verification ---");
        Quantity<WeightUnit> weight = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        // The following line uses Object to allow the comparison call for verification
        boolean crossCompare = oneFeet.equals(weight);
        System.out.println("Compare Feet to Kg -> " + (crossCompare ? "FAIL (Allowed)" : "PASS (Prevented)"));
    }
}