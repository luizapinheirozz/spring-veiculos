package com.veic.Veiculos.Santana.controllers;

import com.veic.Veiculos.Santana.models.Veiculo;
import com.veic.Veiculos.Santana.models.VeiculoDto;
import com.veic.Veiculos.Santana.services.VeiculosRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/veiculos")
public class VeiculosController {

    @Autowired
    private VeiculosRepository repo;

    @GetMapping({"", "/"})
    public String showVeiculosList(@RequestParam(required = false) String termoPesquisa, Model model) {
        List<Veiculo> veiculos;
        if (termoPesquisa != null && !termoPesquisa.isEmpty()) {
            veiculos = repo.findByModeloOrAno(termoPesquisa, termoPesquisa);
        } else {
            veiculos = repo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        }

        // Extrair categorias únicas dos veículos
        Set<String> categorias = repo.findDistinctCategorias();

        model.addAttribute("veiculos", veiculos);
        model.addAttribute("categorias", categorias);

        return "veiculos/index";
    }



    @GetMapping("/create")
    public String showCreatePage(Model model) {
        VeiculoDto veiculoDto = new VeiculoDto();
        model.addAttribute("veiculoDto", veiculoDto);
        return "veiculos/CreateVeiculo";
    }

    @PostMapping("/create")
    public String Veiculo(@Valid @ModelAttribute VeiculoDto veiculoDto, BindingResult result) {

        if (veiculoDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("veiculoDto", "imageFile", "Insira a imagem"));
        }

        if (result.hasErrors()) {
            return "veiculos/CreateVeiculo";
        }

        // Save image file
        MultipartFile image = veiculoDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Veiculo veiculo = new Veiculo();
        veiculo.setMarca(veiculoDto.getMarca());
        veiculo.setModelo(veiculoDto.getModelo());
        veiculo.setCategoria(veiculoDto.getCategoria());
        veiculo.setCor(veiculoDto.getCor());
        veiculo.setPlaca(veiculoDto.getPlaca());
        veiculo.setAno(veiculoDto.getAno());
        veiculo.setDescricao(veiculoDto.getDescricao());
        veiculo.setDataCriacao(createdAt);
        veiculo.setImageFileName(storageFileName);

        repo.save(veiculo);

        return "redirect:/veiculos";
    }


    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {

        try {
            Veiculo veiculo = repo.findById(id).get();
            model.addAttribute("veiculo", veiculo);

            VeiculoDto veiculoDto = new VeiculoDto();
            veiculoDto.setMarca(veiculo.getMarca());
            veiculoDto.setModelo(veiculo.getModelo());
            veiculoDto.setCategoria(veiculo.getCategoria());
            veiculoDto.setCor(veiculo.getCor());
            veiculoDto.setPlaca(veiculo.getPlaca());
            veiculoDto.setAno(veiculo.getAno());
            veiculoDto.setDescricao(veiculo.getDescricao());

            model.addAttribute("veiculoDto", veiculoDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/veiculos";
        }

        return "veiculos/EditVeiculo";
    }

    @PostMapping("/edit")
    public String updateVeiculo(Model model,
                                @RequestParam int id,
                                @Valid @ModelAttribute VeiculoDto veiculoDto,
                                BindingResult result) {
        try {
            Veiculo veiculo = repo.findById(id).get();
            model.addAttribute("veiculo", veiculo);

            if (result.hasErrors()) {
                return "veiculos/EditVeiculo";
            }

            if (!veiculoDto.getImageFile().isEmpty()) {
                // Delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + veiculo.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                // save new image file
                MultipartFile image = veiculoDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                veiculo.setImageFileName(storageFileName);
            }

            veiculo.setMarca(veiculoDto.getMarca());
            veiculo.setModelo(veiculoDto.getModelo());
            veiculo.setCategoria(veiculoDto.getCategoria());
            veiculo.setCor(veiculoDto.getCor());
            veiculo.setPlaca(veiculoDto.getPlaca());
            veiculo.setAno(veiculoDto.getAno());
            veiculo.setDescricao(veiculoDto.getDescricao());

            repo.save(veiculo);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/veiculos/";
    }

    @GetMapping("/delete")
    public String deleteVeiculo(@RequestParam int id) {
        try {
            Veiculo veiculo = repo.findById(id).get();

            // Delete vehicle image
            Path imagePath = Paths.get("public/images/" + veiculo.getImageFileName());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            // Delete the vehicle
            repo.delete(veiculo);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/veiculos";
    }


    @GetMapping("/categoria")
    public String showVeiculosByCategoria(@RequestParam String categoria, Model model) {
        List<Veiculo> veiculos = repo.findByCategoria(categoria);
        Set<String> categorias = repo.findDistinctCategorias();
        model.addAttribute("veiculos", veiculos);
        model.addAttribute("categorias", categorias);
        return "veiculos/index";
    }

    @GetMapping("/detalhes/{id}")
    public String showVeiculoDetails(@PathVariable int id, Model model) {
        Veiculo veiculo = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        model.addAttribute("veiculo", veiculo);
        return "veiculos/details";
    }

    @GetMapping("/categorias")
    public String showCategorias(Model model) {
        return "veiculos/categorias";
    }


}
