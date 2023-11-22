package ro.ase.costin.ecomcommon.data;

public enum OrderStatus {

    NOUA {
        @Override
        public String defaultDescription() {
            return "Comanda a fost plasată";
        }
    },

    ANULATA {
        @Override
        public String defaultDescription() {
            return "Comanda a fost anulată";
        }
    },

    PROCESARE {
        @Override
        public String defaultDescription() {
            return "În procesare";
        }
    },

    AMBALARE {
        @Override
        public String defaultDescription() {
            return "Comanda este pregătită";
        }
    },

    RIDICATA {
        @Override
        public String defaultDescription() {
            return "Preluat de livrator";
        }
    },

    LIVRARE {
        @Override
        public String defaultDescription() {
            return "În proces de livrare";
        }
    },

    LIVRATA {
        @Override
        public String defaultDescription() {
            return "Comanda a fost livrată";
        }
    },

    RETUR_SOLICITAT {
        @Override
        public String defaultDescription() {
            return "Retur solicitat";
        }
    },

    RETURNATA {
        @Override
        public String defaultDescription() {
            return "Comanda a fost stornată";
        }
    },

    PLATITA {
        @Override
        public String defaultDescription() {
            return "Comanda este plătită";
        }
    },

    RAMBURSATA {
        @Override
        public String defaultDescription() {
            return "Returul este rambursat";
        }
    };

    public abstract String defaultDescription();
}
