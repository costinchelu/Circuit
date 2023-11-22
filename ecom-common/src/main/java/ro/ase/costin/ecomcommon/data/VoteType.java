package ro.ase.costin.ecomcommon.data;

public enum VoteType {

    UP {
        @Override
        public String toString() {
            return "up";
        }
    },

    DOWN {
        @Override
        public String toString() {
            return "down";
        }
    }
}
