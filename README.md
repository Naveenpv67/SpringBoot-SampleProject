@Override
public AddConsentResponse clone() {
    try {
        AddConsentResponse clonedObject = (AddConsentResponse) super.clone();
        // You may also perform deep or shallow cloning as needed
        return clonedObject;
    } catch (CloneNotSupportedException e) {
        // Handle any exceptions, but this is unlikely to occur with Cloneable
        return null;
    }
}
