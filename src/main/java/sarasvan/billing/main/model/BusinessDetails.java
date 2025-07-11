package sarasvan.billing.main.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDetails {

    private Long id;
    private String businessName;
    private String address;
    private String gstin;
    private String pan;
    private String email;
    private String phone;
}
