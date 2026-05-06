package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class Member {

    private String id;
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String gender;

    private String address;
    private String profession;
    private String phone;
    private String email;

    private LocalDate membershipDate;
    private String collectivityId;

    public Member() {}

    public Member(String id, String lastName, String firstName, LocalDate birthDate,
                  String gender, String address, String profession, String phone,
                  String email, LocalDate membershipDate, String collectivityId) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.profession = profession;
        this.phone = phone;
        this.email = email;
        this.membershipDate = membershipDate;
        this.collectivityId = collectivityId;
    }
}