class Rabbit {

    //Somewhere create Getters and Setters for each thing so it can be accessed and manipulated by other programs
    int id = 0;
    int sex = 0;
    int age = 0;
    int preg = 0;
    int wait = 0;

    Rabbit(int sex, int age, int preg, int wait){
        this.sex = sex;
        this.age = age;
        this.wait = wait;
        this.preg = preg;
    }
    public void setSex(int sex){
        this.sex = sex;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setPreg(int preg){
        this.preg = preg;
    }
    public void setWait(int wait){
        this.wait = wait;
    }
    public int getPreg(){
        return preg;
    }
    public int getWait(){
        return wait;
    }
    public int getAge(){
        return age;
    }
    public int getSex(){
        return sex;
    }
    //creates a method to create a litter of new rabbits to add to the list

}
