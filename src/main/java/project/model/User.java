package project.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id_user;
    String pseudo;
    String pwd;
    @Column(name="email", unique=true)
    String email;
    int credit = 0;
    int mmr = 0;
    int nbrWin = 0;
    int nbrLoss = 0;
    int nbrTourn = 0;
    int nbrTournWin = 0;

    @ManyToMany
    List<Objet> objets;

    @OneToMany
    List<Partie> parties;

    @ManyToMany
    List<User> amis;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Roles> roles;

    public User() {

    }

    public User(String pseudo, String pwd, String email, List<Roles> roles) {
        this.pseudo = pseudo;
        this.pwd = pwd;
        this.email = email;
        this.roles= roles;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getMmr() {
        return mmr;
    }

    public void setMmr(int mmr) {
        this.mmr = mmr;
    }

    public int getNbrWin() {
        return nbrWin;
    }

    public void setNbrWin(int nbrWin) {
        this.nbrWin = nbrWin;
    }

    public int getNbrLoss() {
        return nbrLoss;
    }

    public void setNbrLoss(int nbrLoss) {
        this.nbrLoss = nbrLoss;
    }

    public int getNbrTourn() {
        return nbrTourn;
    }

    public void setNbrTourn(int nbrTourn) {
        this.nbrTourn = nbrTourn;
    }

    public int getNbrTournWin() {
        return nbrTournWin;
    }

    public void setNbrTournWin(int nbrTournWin) {
        this.nbrTournWin = nbrTournWin;
    }

    public List<Objet> getObjets() {
        return objets;
    }

    public List<Partie> getParties() {
        return parties;
    }

    public List<User> getAmis() {
        return amis;
    }
}
