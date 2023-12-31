package top.jrhong.library.entitys;

import lombok.Data;

import javax.persistence.*;

/**
 * 用户实体类
 * @author KSaMar
 */
@Table(name = "USERLIST")
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_groups")
    private String groups;
    private String name;
    private String username;
    private String password;
    private String email;
    private String code;
//    private String gender;
    @Column(name = "id_card")
    private String idCard;
    private String phone;
    private String identity;
    @Column(name = "book_count")
    private Integer bookCount;
    private Integer state;

}
