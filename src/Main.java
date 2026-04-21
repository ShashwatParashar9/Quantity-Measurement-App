package com.apps.quantitymeasurement;

public class Main {

    // ================= ENUM =================
    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double toFeet(double value) {
            return value * toFeet;
        }

        public double fromFeet(double feetValue) {
            return feetValue / toFeet;
        }
    }

    // ================= CLASS =================
    public static class QuantityLength {
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

            double thisFeet = this.unit.toFeet(this.value);
            double otherFeet = other.unit.toFeet(other.value);

            return Double.compare(thisFeet, otherFeet) == 0;
        }

        // ===== CONVERSION (INSTANCE METHOD) =====
        public QuantityLength convertTo(LengthUnit targetUnit) {
            double valueInFeet = this.unit.toFeet(this.value);
            double converted = targetUnit.fromFeet(valueInFeet);
            return new QuantityLength(converted, targetUnit);
        }

        // ===== STATIC CONVERSION =====
        public static double convert(double value, LengthUnit source, LengthUnit target) {

            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid value");
            }
            if (source == null || target == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }

            double valueInFeet = source.toFeet(value);
            return target.fromFeet(valueInFeet);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ================= DEMO METHODS =================

    // Overloaded Method 1
    public static void demonstrateLengthConversion(double value, LengthUnit from, LengthUnit to) {
        double result = QuantityLength.convert(value, from, to);
        System.out.println("convert(" + value + ", " + from + ", " + to + ") = " + result);
    }

    // Overloaded Method 2
    public static void demonstrateLengthConversion(QuantityLength q, LengthUnit to) {
        QuantityLength result = q.convertTo(to);
        System.out.println(q + " → " + result);
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        // Basic conversions
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCH);
        demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCH, LengthUnit.YARDS);
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCH);

        // Using object
        QuantityLength q = new QuantityLength(2.0, LengthUnit.YARDS);
        demonstrateLengthConversion(q, LengthUnit.INCH);

        // Equality check
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);
        System.out.println("Equality: " + q1.equals(q2));
    }
}