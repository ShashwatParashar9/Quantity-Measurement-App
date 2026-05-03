package com.apps.quantitymeasurement;

import java.util.Objects;

/**
 * UC12: Subtraction and Division Operations
 * This standalone code includes all units (Length, Weight, Volume) and
 * arithmetic operations without requiring any external libraries like JUnit.
 */

// --- STEP 1: IMeasurable Interface ---
interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

// --- STEP 2: Unit Enums ---
enum LengthUnit implements IMeasurable {
    FEET(12.0), INCHES(1.0), YARDS(36.0);
    private final double factor;
    LengthUnit(double factor) { this.factor = factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
    @Override public String getUnitName() { return this.name(); }
}

enum WeightUnit implements IMeasurable {
    GRAM(0.001), KILOGRAM(1.0), TONNE(1000.0);
    private final double factor;
    WeightUnit(double factor) { this.factor = factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
    @Override public String getUnitName() { return this.name(); }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0), MILLILITRE(0.001), GALLON(3.78541);
    private final double factor;
    VolumeUnit(double factor) { this.factor = factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
    @Override public String getUnitName() { return this.name(); }
}

// --- STEP 3: Generic Quantity Class ---
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    private double getBaseValue() {
        return unit.convertToBaseUnit(this.value);
    }

    // UC12: Subtraction
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateCategory(other);
        double resultInBase = this.getBaseValue() - other.getBaseValue();
        double converted = targetUnit.convertFromBaseUnit(resultInBase);
        // Round to two decimal places
        double rounded = Math.round(converted * 100.0) / 100.0;
        return new Quantity<>(rounded, targetUnit);
    }

    // UC12: Division (returns dimensionless ratio)
    public double divide(Quantity<U> other) {
        validateCategory(other);
        if (other.value == 0) throw new ArithmeticException("Division by zero is not allowed");
        return this.getBaseValue() / other.getBaseValue();
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validateCategory(other);
        double sumInBase = this.getBaseValue() + other.getBaseValue();
        return new Quantity<>(targetUnit.convertFromBaseUnit(sumInBase), targetUnit);
    }

    private void validateCategory(Quantity<U> other) {
        if (other == null) throw new IllegalArgumentException("Operand cannot be null");
        // Generic U ensures categories match at compile-time
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity<?> that = (Quantity<?>) o;
        // Cross-category safety check at runtime
        if (this.unit.getClass() != that.unit.getClass()) return false;
        return Math.abs(this.getBaseValue() - ((IMeasurable)that.unit).convertToBaseUnit((Double)that.value)) < 1e-5;
    }

    @Override public int hashCode() { return Objects.hash(value, unit); }
    @Override public String toString() { return value + " " + unit.getUnitName(); }
}

// --- STEP 4: QuantityMeasurementApp Class ---
public class QuantityMeasurementApp {

    // Using wildcards (?) allows checking equality between DIFFERENT categories without a compiler error
    public static void demonstrateEquality(Quantity<?> q1, Quantity<?> q2) {
        System.out.println("Equality: " + q1 + " == " + q2 + " -> " + q1.equals(q2));
    }

    public static <U extends IMeasurable> void demonstrateSubtraction(Quantity<U> q1, Quantity<U> q2) {
        Quantity<U> result = q1.subtract(q2);
        System.out.println("Subtraction: " + q1 + " - " + q2 + " = " + result);
    }

    public static <U extends IMeasurable> void demonstrateDivision(Quantity<U> q1, Quantity<U> q2) {
        double ratio = q1.divide(q2);
        System.out.println("Division: " + q1 + " / " + q2 + " = " + ratio);
    }

    public static void main(String[] args) {
        System.out.println("=== Standalone Quantity Measurement App (UC12) ===\n");

        // 1. Equality Demos
        Quantity<LengthUnit> ft = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> in = new Quantity<>(12.0, LengthUnit.INCHES);
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);

        demonstrateEquality(ft, in); // Should be true
        demonstrateEquality(ft, kg); // Should be false (Cross-category)

        // 2. Subtraction Demos (Implicit & Explicit)
        System.out.println("\n--- Subtraction ---");
        demonstrateSubtraction(new Quantity<>(10.0, LengthUnit.FEET), new Quantity<>(6.0, LengthUnit.INCHES));

        Quantity<VolumeUnit> litre = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> ml = new Quantity<>(500.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> subResult = litre.subtract(ml, VolumeUnit.MILLILITRE);
        System.out.println("Explicit Subtraction: 5L - 500mL = " + subResult);

        // 3. Division Demos
        System.out.println("\n--- Division ---");
        demonstrateDivision(new Quantity<>(24.0, LengthUnit.INCHES), new Quantity<>(2.0, LengthUnit.FEET));
        demonstrateDivision(new Quantity<>(2000.0, WeightUnit.GRAM), new Quantity<>(1.0, WeightUnit.KILOGRAM));

        System.out.println("\n=== All Operations Completed Successfully ===");
    }
}
