import java.io.*;

public class AddConsentResponseResetter {
    public static AddConsentResponse reset(AddConsentResponse original) throws IOException, ClassNotFoundException {
        // Serialize the original object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(original);
        out.close();

        // Deserialize the serialized object into a new instance
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        AddConsentResponse newObject = (AddConsentResponse) in.readObject();
        in.close();



        return newObject;
    }
}


AddConsentResponse original = AddConsentResponse.getInstance();
// ... Modify the original object as needed

AddConsentResponse resetObject = AddConsentResponseResetter.reset(original);




