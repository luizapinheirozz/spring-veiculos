package com.veic.Veiculos.Santana.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class VeiculoDto {
    @NotEmpty(message = "Insira a marca")
    private String marca;

    @NotEmpty(message = "Insira o modelo")
    private String modelo;

    @NotEmpty(message = "Insira a categoria")
    private String categoria;

    @NotEmpty(message = "Insira a cor")
    private String cor;

    @NotEmpty(message = "Insira a placa")
    private String placa;

    @NotEmpty(message = "Insira o ano")
    private String ano;

    @Size(min = 10, message = "A descrição deve conter mais de 10 caracteres")
    @Size(max = 2000, message = "A descrição não pode exeder 2000 caracteres")
    private String descricao;

    private MultipartFile imageFile;

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
