package uni.fmi.masters.service;

import uni.fmi.masters.model.User;
import uni.fmi.masters.repository.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Hashes a password using SHA-256 with a given salt.
     * 
     * @param password  The plain text password.
     * @param saltBytes The byte array salt to use.
     * @return The Base64 encoded hashed password.
     */
    public String hashPassword(String password, byte[] saltBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltBytes);
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password.", e);
        }
    }

    /**
     * Generates a cryptographically secure random salt.
     * 
     * @return A byte array representing the salt.
     */
    public byte[] generateRandomSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public boolean registerUser(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required for registration.");
            return false;
        }

        if (!email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            System.out.println("Invalid email format.");
            return false;
        }

        if (userRepository.findByUsername(username).isPresent()) {
            System.out.println("Username already exists.");
            return false;
        }

        byte[] saltBytes = generateRandomSalt();
        String encodedSalt = Base64.getEncoder().encodeToString(saltBytes);

        String hashedPassword = hashPassword(password, saltBytes);

        User newUser = new User(username, email, hashedPassword, encodedSalt);
        boolean success = userRepository.createUser(newUser);
        if (success) {
            System.out.println("User registered successfully: " + username);
        } else {
            System.out.println("User registration failed. Username or email might be taken.");
        }
        return success;
    }

    public Optional<User> loginUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            byte[] storedSaltBytes = Base64.getDecoder().decode(user.getSalt());

            String hashedPasswordAttempt = hashPassword(password, storedSaltBytes);

            if (hashedPasswordAttempt.equals(user.getPasswordHash())) {
                return Optional.of(user);
            }
        } else {
            System.out.println("User not found: " + username);
        }
        return Optional.empty();
    }
}