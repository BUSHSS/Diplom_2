public class DataLoginUser {
    private String email;
    private String password;

    // конструктор со всеми параметрами
    public DataLoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }


    // конструктор без параметров
    public DataLoginUser() {
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
