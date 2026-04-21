package com.apps.quantitymeasurement;

public class Main{

    // ================= LENGTH ENUM =================
    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double toBase(double value) {
            return value * toFeet;
        }

        public double fromBase(double base) {
            return base / toFeet;
        }
    }

    // ================= WEIGHT ENUM =================
    enum WeightUnit {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double toKg;

        WeightUnit(double toKg) {
            this.toKg = toKg;
        }

        public double toBase(double value) {
            return value * toKg;
        }

        public double fromBase(double base) {
            return base / toKg;
        }
    }

    // ================= LENGTH CLASS =================
    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            this.value = value;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            QuantityLength other = (QuantityLength) obj;

            return Double.compare(
                    unit.toBase(value),
                    other.unit.toBase(other.value)
            ) == 0;
        }
    }

    // ================= WEIGHT CLASS =================
    static class QuantityWeight {
        private final double value;
        private final WeightUnit unit;

        public QuantityWeight(double value, WeightUnit unit) {
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
            if (obj == null || getClass() != obj.getClass()) return false;

            QuantityWeight other = (QuantityWeight) obj;

            return Double.compare(
                    unit.toBase(value),
                    other.unit.toBase(other.value)
            ) == 0;
        }

        // ===== CONVERT =====
        public QuantityWeight convertTo(WeightUnit targetUnit) {
            double base = unit.toBase(value);
            double result = targetUnit.fromBase(base);
            return new QuantityWeight(result, targetUnit);
        }

        // ===== ADD (UC6 style) =====
        public QuantityWeight add(QuantityWeight other) {
            double sumBase =
                    unit.toBase(value) +
                            other.unit.toBase(other.value);

            double result = unit.fromBase(sumBase);
            return new QuantityWeight(result, unit);
        }

        // ===== ADD WITH TARGET (UC7 style) =====
        public QuantityWeight add(QuantityWeight other, WeightUnit targetUnit) {
            double sumBase =
                    unit.toBase(value) +
                            other.unit.toBase(other.value);

            double result = targetUnit.fromBase(sumBase);
            return new QuantityWeight(result, targetUnit);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        // ===== WEIGHT EQUALITY =====
        QuantityWeight w1 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight w2 = new QuantityWeight(1000.0, WeightUnit.GRAM);

        System.out.println("1 kg == 1000 g → " + w1.equals(w2));

        // ===== CONVERSION =====
        System.out.println("1 kg in pounds → " +
                w1.convertTo(WeightUnit.POUND));

        // ===== ADDITION =====
        QuantityWeight w3 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight w4 = new QuantityWeight(500.0, WeightUnit.GRAM);

        System.out.println("1 kg + 500 g → " + w3.add(w4));

        // ===== ADD WITH TARGET =====
        System.out.println("1 kg + 500 g in pounds → " +
                w3.add(w4, WeightUnit.POUND));
    }
}