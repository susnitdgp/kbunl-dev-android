package kbunl.com.kbunl_dev.model;



public class ItComplainRoles {

    private Boolean ItComplainSupervisor;
    private Boolean ItComplainServiceEng;
    private Boolean ItComplainUser;

    public ItComplainRoles(){
        this.ItComplainUser=true;

        this.ItComplainServiceEng=false;
        this.ItComplainSupervisor=false;
    }

    public Boolean getItComplainSupervisor() {
        return ItComplainSupervisor;
    }

    public void setItComplainSupervisor(Boolean itComplainSupervisor) {
        ItComplainSupervisor = itComplainSupervisor;
    }

    public Boolean getItComplainServiceEng() {
        return ItComplainServiceEng;
    }

    public void setItComplainServiceEng(Boolean itComplainServiceEng) {
        ItComplainServiceEng = itComplainServiceEng;
    }

    public Boolean getItComplainUser() {
        return ItComplainUser;
    }

    public void setItComplainUser(Boolean itComplainUser) {
        ItComplainUser = itComplainUser;
    }
}
