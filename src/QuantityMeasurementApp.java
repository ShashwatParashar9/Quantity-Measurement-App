package com.apps.quantitymeasurement;

import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

/**
 * UC13: Centralized Arithmetic Logic to Enforce DRY in Quantity Operations.
 * Refactors addition, subtraction, and division to eliminate code duplication.
 */

// --- STEP 1: IMeasurable Interface ---
interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

// --- STEP 2: Unit Enums (Length, Weight, Volume) ---
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

// --- STEP 3: Generic Quantity Class with Centralized Logic ---
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    // --- UC13 Step 1: ArithmeticOperation Enum using Lambda ---
    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return a / b;
        });

        private final DoubleBinaryOperator operator;
        ArithmeticOperation(DoubleBinaryOperator operator) {
            this.operator = operator;
        }
        public double compute(double a, double b) {
            return operator.applyAsDouble(a, b);
        }
    }

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    // --- UC13 Step 2: Centralized Validation Helper ---
    private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetUnitRequired) {
        if (other == null) throw new IllegalArgumentException("Operand cannot be null");
        if (this.unit.getClass() != other.unit.getClass()) {
            throw new IllegalArgumentException("Cross-category arithmetic prevented");
        }
        if (!Double.isFinite(this.value) || !Double.isFinite(other.value)) {
            throw new IllegalArgumentException("Values must be finite");
        }
        if (targetUnitRequired && targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
    }

    // --- UC13 Step 3: Core Arithmetic Helper Method ---
    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
        double v1 = this.unit.convertToBaseUnit(this.value);
        double v2 = other.unit.convertToBaseUnit(other.value);
        return operation.compute(v1, v2);
    }

    private double roundToTwoDecimals(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    // --- UC13 Step 4: Refactored Public Arithmetic Methods ---
    public Quantity<U> add(Quantity<U> other) { return add(other, this.unit); }
    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        double resultBase = performBaseArithmetic(other, ArithmeticOperation.ADD);
        return new Quantity<>(roundToTwoDecimals(targetUnit.convertFromBaseUnit(resultBase)), targetUnit);
    }

    public Quantity<U> subtract(Quantity<U> other) { return subtract(other, this.unit); }
    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        double resultBase = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
        return new Quantity<>(roundToTwoDecimals(targetUnit.convertFromBaseUnit(resultBase)), targetUnit);
    }

    public double divide(Quantity<U> other) {
        validateArithmeticOperands(other, null, false);
        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity<?> that = (Quantity<?>) o;
        if (this.unit.getClass() != that.unit.getClass()) return false;
        return Math.abs(this.unit.convertToBaseUnit(this.value) -
                ((IMeasurable)that.unit).convertToBaseUnit((Double)that.value)) < 1e-5;
    }

    @Override public int hashCode() { return Objects.hash(value, unit); }
    @Override public String toString() { return "Quantity(" + value + ", " + unit.getUnitName() + ")"; }
}

// --- STEP 5: QuantityMeasurementApp Class ---
public class QuantityMeasurementApp {

    public static <U extends IMeasurable> void demonstrateOperation(String label, Quantity<U> q1, Quantity<U> q2, U target) {
        try {
            if (label.equals("DIVIDE")) {
                double result = q1.divide(q2);
                System.out.println("Result: " + q1 + " / " + q2 + " = " + result);
            } else if (label.equals("SUBTRACT")) {
                Quantity<U> result = q1.subtract(q2, target);
                System.out.println("Result: " + q1 + " - " + q2 + " = " + result);
            } else {
                Quantity<U> result = q1.add(q2, target);
                System.out.println("Result: " + q1 + " + " + q2 + " = " + result);
            }
        } catch (Exception e) {
            System.out.println("Error (" + label + "): " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== UC13 Refactored Centralized Logic Demo ===\n");

        Quantity<LengthUnit> ft10 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> in6 = new Quantity<>(6.0, LengthUnit.INCHES);

        // Subtraction with implicit target (UC12 behavior preserved)
        demonstrateOperation("SUBTRACT", ft10, in6, LengthUnit.FEET); // 9.5 FEET

        // Division (dimensionless result)
        demonstrateOperation("DIVIDE", new Quantity<>(24.0, LengthUnit.INCHES), new Quantity<>(2.0, LengthUnit.FEET), null); // 1.0

        // Validation Consistency (centralized check)
        System.out.println("\n--- Validation Consistency Check ---");
        demonstrateOperation("ADD", ft10, null, null);
        demonstrateOperation("DIVIDE", ft10, new Quantity<>(0.0, LengthUnit.FEET), null);
    }
}

