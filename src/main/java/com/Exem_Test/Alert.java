package com.Exem_Test;

//import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.*;
import lombok.*;

//import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;
    private String dateTime;
    private String area;
    private int phase;

}
