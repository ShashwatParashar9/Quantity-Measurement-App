package com.apps.quantitymeasurement;

public class Main {

    // ================= ENUM (Top-level inside same file) =================
    // NOTE: Not inside QuantityLength → satisfies UC8 idea
    enum LengthUnit {

        FEET(1.0),
        INCH(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        // Convert THIS unit → base (feet)
        public double convertToBaseUnit(double value) {
            return value * toFeetFactor;
        }

        // Convert base (feet) → THIS unit
        public double convertFromBaseUnit(double baseValue) {
            return baseValue / toFeetFactor;
        }
    }

    // ================= CLASS =================
    static class QuantityLength {

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid value");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            this.value = value;
            this.unit = unit;
        }

        // ===== EQUALITY =====
        @Override
        public boolean equals(Object obj) {

            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;

            QuantityLength other = (QuantityLength) obj;

            double thisBase = unit.convertToBaseUnit(value);
            double otherBase = other.unit.convertToBaseUnit(other.value);

            return Double.compare(thisBase, otherBase) == 0;
        }

        // ===== CONVERT =====
        public QuantityLength convertTo(LengthUnit targetUnit) {
            double base = unit.convertToBaseUnit(value);
            double result = targetUnit.convertFromBaseUnit(base);
            return new QuantityLength(result, targetUnit);
        }

        // ===== STATIC CONVERT =====
        public static double convert(double value, LengthUnit source, LengthUnit target) {
            double base = source.convertToBaseUnit(value);
            return target.convertFromBaseUnit(base);
        }

        // ===== ADD (UC6) =====
        public QuantityLength add(QuantityLength other) {
            double sumBase =
                    unit.convertToBaseUnit(value) +
                            other.unit.convertToBaseUnit(other.value);

            double result = unit.convertFromBaseUnit(sumBase);
            return new QuantityLength(result, unit);
        }

        // ===== ADD WITH TARGET (UC7) =====
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            double sumBase =
                    unit.convertToBaseUnit(value) +
                            other.unit.convertToBaseUnit(other.value);

            double result = targetUnit.convertFromBaseUnit(sumBase);
            return new QuantityLength(result, targetUnit);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);

        // Equality
        System.out.println("Equal: " + q1.equals(q2));

        // Conversion
        System.out.println("Convert: " +
                QuantityLength.convert(1.0, LengthUnit.FEET, LengthUnit.INCH));

        // Addition (UC6)
        System.out.println("Add: " + q1.add(q2));

        // Addition with target (UC7)
        System.out.println("Add in YARDS: " + q1.add(q2, LengthUnit.YARDS));
    }
}