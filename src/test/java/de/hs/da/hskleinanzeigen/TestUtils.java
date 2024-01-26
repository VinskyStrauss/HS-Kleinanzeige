package de.hs.da.hskleinanzeigen;
import de.hs.da.hskleinanzeigen.entity.*;

public class TestUtils {
    public static final String BASE_PATH_CATEGORY = "/api/categories";
    public static final String BASE_PATH_USER = "/api/users";
    public static final String BASE_PATH_NOTEPAD = "/api/users/{userId}/notepad";
    public static final String BASE_PATH_AD = "/api/advertisements";

    public static Category createCategory(String name){
        Category category = new Category();
        category.setName(name);
        return category;
    }

    public static User createUser(String email, String firstName, String lastName, String location, String password, String phone){
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLocation(location);
        user.setPassword(password);
        user.setPhone(phone);
        return user;
    }

    public static Advertisement createAd(AdType type, Category category, User user, String title, String description, int price, String location){
        Advertisement advertisement = new Advertisement();
        advertisement.setType(type);
        advertisement.setCategory(category);
        advertisement.setUser(user);
        advertisement.setTitle(title);
        advertisement.setDescription(description);
        advertisement.setPrice(price);
        advertisement.setLocation(location);
        return advertisement;
    }

    public static Notepad createNotepad(User user, Advertisement advertisement, String note){
        Notepad notepad = new Notepad();
        notepad.setUser(user);
        notepad.setAdvertisement(advertisement);
        notepad.setNote(note);
        return notepad;
    }
}