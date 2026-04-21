package com.apps.quantitymeasurement;

public class Main {

    // ================= ENUM =================
    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084); // 1 cm = 0.0328084 feet

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double toFeet(double value) {
            return value * toFeet;
        }
    }

    // ================= GENERIC CLASS =================
    public static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            this.value = value;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) return true;

            if (obj == null) return false;

            if (getClass() != obj.getClass()) return false;

            QuantityLength other = (QuantityLength) obj;

            double thisInFeet = this.unit.toFeet(this.value);
            double otherInFeet = other.unit.toFeet(other.value);

            return Double.compare(thisInFeet, otherInFeet) == 0;
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        // Yard to Feet
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q2 = new QuantityLength(3.0, LengthUnit.FEET);
        System.out.println("1 yard == 3 feet → " + q1.equals(q2));

        // Yard to Inches
        QuantityLength q3 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q4 = new QuantityLength(36.0, LengthUnit.INCH);
        System.out.println("1 yard == 36 inches → " + q3.equals(q4));

        // CM to Inches
        QuantityLength q5 = new QuantityLength(1.0, LengthUnit.CENTIMETERS);
        QuantityLength q6 = new QuantityLength(0.393701, LengthUnit.INCH);
        System.out.println("1 cm == 0.393701 inch → " + q5.equals(q6));

        // Same unit
        QuantityLength q7 = new QuantityLength(2.0, LengthUnit.YARDS);
        QuantityLength q8 = new QuantityLength(2.0, LengthUnit.YARDS);
        System.out.println("2 yard == 2 yard → " + q7.equals(q8));
    }
}