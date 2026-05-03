package com.apps.quantitymeasurement;

import java.util.Objects;

/**
 * UC11: Volume Measurement Equality, Conversion, and Addition.
 * Supports Litre, Millilitre, and Gallon categories.
 */

// --- STEP 1: IMeasurable Interface (UC10) ---
interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

// --- STEP 2: Length and Weight Enums (UC1-UC9) ---
enum LengthUnit implements IMeasurable {
    FEET(12.0), INCHES(1.0), YARDS(36.0);
    private final double factor;
    LengthUnit(double factor) { this.factor = factor; }
    @Override public double getConversionFactor() { return factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
    @Override public String getUnitName() { return this.name(); }
}

enum WeightUnit implements IMeasurable {
    GRAM(0.001), KILOGRAM(1.0), TONNE(1000.0);
    private final double factor;
    WeightUnit(double factor) { this.factor = factor; }
    @Override public double getConversionFactor() { return factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
    @Override public String getUnitName() { return this.name(); }
}

// --- STEP 3: New VolumeUnit Enum (UC11) ---
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),            // Base Unit
    MILLILITRE(0.001),      // 1 mL = 0.001 L
    GALLON(3.78541);        // 1 Gallon ≈ 3.78541 L

    private final double conversionFactor;
    VolumeUnit(double conversionFactor) { this.conversionFactor = conversionFactor; }

    @Override public double getConversionFactor() { return conversionFactor; }
    @Override public double convertToBaseUnit(double value) { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double baseValue) { return baseValue / conversionFactor; }
    @Override public String getUnitName() { return this.name(); }
}

// --- STEP 4: Generic Quantity Class (UC10) ---
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;
    private final double epsilon = 1e-5; // For floating point precision

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        this.value = value;
        this.unit = unit;
    }

    public Quantity<U> convertTo(U targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(baseValue), targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit); // Implicit target is first operand's unit
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(sumInBase), targetUnit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Reflexive
        if (o == null || getClass() != o.getClass()) return false;
        Quantity<?> that = (Quantity<?>) o;
        // Cross-category prevention
        if (this.unit.getClass() != that.unit.getClass()) return false;

        double v1 = this.unit.convertToBaseUnit(this.value);
        double v2 = ((IMeasurable)that.unit).convertToBaseUnit((Double)that.value);
        return Math.abs(v1 - v2) < epsilon;
    }

    @Override public int hashCode() { return Objects.hash(value, unit); }
    @Override public String toString() { return value + " " + unit.getUnitName(); }
}

// --- STEP 5: Main Application Demonstration ---
public class QuantityMeasurementApp {
    public static void main(String[] args) {
        System.out.println("=== UC11 Volume Measurement Demonstration ===");

        // Equality Comparisons
        Quantity<VolumeUnit> oneLitre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> thousandMl = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        System.out.println("1.0 Litre equals 1000.0 Millilitre: " + oneLitre.equals(thousandMl)); // true

        Quantity<VolumeUnit> oneGallon = new Quantity<>(1.0, VolumeUnit.GALLON);
        Quantity<VolumeUnit> litreEquivalent = new Quantity<>(3.78541, VolumeUnit.LITRE);
        System.out.println("1.0 Gallon equals 3.78541 Litre: " + oneGallon.equals(litreEquivalent)); // true

        // Unit Conversions
        System.out.println("1.0 Gallon to Litre: " + oneGallon.convertTo(VolumeUnit.LITRE)); // 3.78541 L

        // Addition Operations
        Quantity<VolumeUnit> sumLitre = oneLitre.add(thousandMl);
        System.out.println("1.0 Litre + 1000.0 mL (Implicit): " + sumLitre); // 2.0 LITRE

        Quantity<VolumeUnit> sumGallon = oneGallon.add(litreEquivalent, VolumeUnit.GALLON);
        System.out.println("1.0 Gallon + 3.78541 L (Explicit): " + sumGallon); // 2.0 GALLON

        // Category Incompatibility
        Quantity<LengthUnit> oneFoot = new Quantity<>(1.0, LengthUnit.FEET);
        System.out.println("1.0 Litre equals 1.0 Foot: " + oneLitre.equals(oneFoot)); // false
    }
}