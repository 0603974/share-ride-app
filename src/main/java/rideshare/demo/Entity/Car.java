package rideshare.demo.Entity;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;

@Entity
public class Car extends AbstractEntity {

    private Long userId;
    private String make;
    private String model;
    private String category;
    private String color;
    private String plateNumber;
    private Long noSeat;
    private String specialFeature;
    private String carImgUrl;
//    @Lob
//    private ByteArray carImg;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Long getNoSeat() {
        return noSeat;
    }

    public void setNoSeat(Long noSeat) {
        this.noSeat = noSeat;
    }

    public String getSpecialFeature() {
        return specialFeature;
    }

    public void setSpecialFeature(String specialFeature) {
        this.specialFeature = specialFeature;
    }


    public String getCarImgUrl() {
        return carImgUrl;
    }

    public void setCarImgUrl(String carImgUrl) {
        this.carImgUrl = carImgUrl;
    }

    //    public ByteArray getCarImg() {
//        return carImg;
//    }
//
//    public void setCarImg(ByteArray carImg) {
//        this.carImg = carImg;
//    }

    @Override
    public String toString() {
        return "Car{" +
                "userId=" + userId +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", category='" + category + '\'' +
                ", color='" + color + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", noSeat=" + noSeat +
                ", specialFeature='" + specialFeature + '\'' +
                ", carImgUrl='" + carImgUrl + '\'' +
                '}';
    }
}
