import java.lang.reflect.Field;

public class VariableNameExtractor {

    // Generic method to get the variable name of an object
    public static String getVariableName(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(object) == object) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null; // Return null if variable name not found
    }

    public static void main(String[] args) {
        // Test the method with an example object
        MyClass myObject = new MyClass();
        String variableName = getVariableName(myObject);

        System.out.println("Variable Name: " + variableName);
    }
}

class MyClass {
    // Example variable
    private String exampleField;
}
