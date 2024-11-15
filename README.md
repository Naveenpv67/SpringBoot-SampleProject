import java.util.Map;
import java.util.HashMap;

public enum State {
    AN("Andaman & Nicobar"),
    AP("Andhra Pradesh"),
    AR("Arunachal Pradesh"),
    AS("Assam"),
    BR("Bihar"),
    CH("Chandigarh"),
    CG("Chhattisgarh"),
    DN("Dadra and Nagar Haveli"),
    DD("Daman & Diu"),
    DL("Delhi"),
    GA("Goa"),
    GJ("Gujarat"),
    HR("Haryana"),
    HP("Himachal Pradesh"),
    JK("Jammu & Kashmir"),
    JH("Jharkhand"),
    KA("Karnataka"),
    KL("Kerala"),
    LD("Lakshadweep"),
    MP("Madhya Pradesh"),
    MH("Maharashtra"),
    MN("Manipur"),
    ML("Meghalaya"),
    MZ("Mizoram"),
    NL("Nagaland"),
    OR("Odisha"),
    PY("Puducherry"),
    PB("Punjab"),
    RJ("Rajasthan"),
    SK("Sikkim"),
    TN("Tamil Nadu"),
    TG("Telangana"),
    TR("Tripura"),
    UP("Uttar Pradesh"),
    UK("Uttarakhand (Uttranchal)"),
    WB("West Bengal"),
    LK("Ladakh");

    private final String stateName;
    private static final Map<String, State> BY_CODE = new HashMap<>();
    private static final Map<String, State> BY_NAME = new HashMap<>();

    // Static block to initialize the lookup maps
    static {
        for (State state : values()) {
            BY_CODE.put(state.name(), state);
            BY_NAME.put(state.getStateName().toLowerCase(), state); // Case-insensitive lookup
        }
    }

    State(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }

    // Lookup by code (O(1))
    public static State getByCode(String code) {
        return BY_CODE.get(code);
    }

    // Lookup by name (O(1) with map)
    public static State getByName(String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    // Unified lookup: Toggle strategy
    public static State getBy(String input, boolean searchByCode) {
        if (searchByCode) {
            return getByCode(input);
        } else {
            return getByName(input);
        }
    }
}
