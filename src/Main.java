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

        // ===== CONVERSION =====
        public QuantityLength convertTo(LengthUnit targetUnit) {
            double valueInFeet = this.unit.toFeet(this.value);
            double converted = targetUnit.fromFeet(valueInFeet);
            return new QuantityLength(converted, targetUnit);
        }

        // ===== STATIC CONVERT =====
        public static double convert(double value, LengthUnit source, LengthUnit target) {
            double valueInFeet = source.toFeet(value);
            return target.fromFeet(valueInFeet);
        }

        // ================= 🔥 ADD METHOD (UC6) =================
        public QuantityLength add(QuantityLength other) {

            if (other == null) {
                throw new IllegalArgumentException("Other value cannot be null");
            }

            // convert both to feet
            double thisFeet = this.unit.toFeet(this.value);
            double otherFeet = other.unit.toFeet(other.value);

            // add
            double sumFeet = thisFeet + otherFeet;

            // convert result to unit of FIRST operand
            double result = this.unit.fromFeet(sumFeet);

            return new QuantityLength(result, this.unit);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        // Example 1: 1 foot + 12 inch = 2 feet
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCH);

        QuantityLength result1 = q1.add(q2);
        System.out.println("1 foot + 12 inch = " + result1);

        // Example 2: 1 yard + 3 feet = 2 yards
        QuantityLength q3 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q4 = new QuantityLength(3.0, LengthUnit.FEET);

        QuantityLength result2 = q3.add(q4);
        System.out.println("1 yard + 3 feet = " + result2);

        // Example 3: 2 cm + 1 inch
        QuantityLength q5 = new QuantityLength(2.0, LengthUnit.CENTIMETERS);
        QuantityLength q6 = new QuantityLength(1.0, LengthUnit.INCH);

        QuantityLength result3 = q5.add(q6);
        System.out.println("2 cm + 1 inch = " + result3);
    }
}