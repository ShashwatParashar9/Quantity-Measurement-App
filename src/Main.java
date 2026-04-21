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

        // ===== ADD (UC6 - default) =====
        public QuantityLength add(QuantityLength other) {
            double sumFeet = this.unit.toFeet(this.value) +
                    other.unit.toFeet(other.value);

            double result = this.unit.fromFeet(sumFeet);
            return new QuantityLength(result, this.unit);
        }

        // ================= 🔥 ADD WITH TARGET (UC7) =================
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {

            if (other == null) {
                throw new IllegalArgumentException("Other cannot be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            // convert both to feet
            double thisFeet = this.unit.toFeet(this.value);
            double otherFeet = other.unit.toFeet(other.value);

            // add
            double sumFeet = thisFeet + otherFeet;

            // convert to target unit
            double result = targetUnit.fromFeet(sumFeet);

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

        // UC6 (default)
        System.out.println("Default add: " + q1.add(q2));

        // UC7 (target unit = YARDS)
        QuantityLength result = q1.add(q2, LengthUnit.YARDS);
        System.out.println("1 foot + 12 inch in YARDS = " + result);
    }
}