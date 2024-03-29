package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.FilesService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    private final FilesService filesService ;
    private final UserService userService;

    public FileUploadController(FilesService filesService, UserService userService){
        this.filesService = filesService;
        this.userService = userService;
    }
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("fileUpload") MultipartFile file,Model model) {
        User user = userService.getUser();
        if(StringUtils.isEmpty(file.getOriginalFilename())){
            model.addAttribute("dataMessage",
                    "No file Selected !");
        } else if(filesService.isFileAlreadyExists(file.getOriginalFilename(),user.getUserId())){
            model.addAttribute("dataMessage",
                    "File Already Exists with the same name " + file.getOriginalFilename() + "!");
        } else {
            int result = filesService.storeFile(file,user.getUserId());
            if(result == 1){
                model.addAttribute("successMessage",
                        "You successfully uploaded " + file.getOriginalFilename() + "!");
            }else {
                model.addAttribute("errorMessage",
                        "Error during uploaded " + file.getOriginalFilename() + "!");
            }
        }
        return "result";
    }

    @GetMapping("/deleteFile")
    public String handleFileDelete(@RequestParam(value = "fileName", required = false) String fileName,Model model) {
        User user = userService.getUser();
        int result = filesService.deleteFile(fileName,user.getUserId());
        if(result == 1){
            model.addAttribute("successMessage",
                    "File successfully deleted " + fileName + "!");
        }else {
            model.addAttribute("errorMessage",
                    "Error during delete " + fileName + "!");
        }
        return "result";
    }

    @GetMapping("/downloadFile")
    public  ResponseEntity <Resource> handleFileView(@RequestParam(value = "fileName", required = false) String fileName) {
        User user = userService.getUser();
        File fileToView = filesService.loadFile(fileName,user.getUserId());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileToView.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileToView.getFileName() + "\"")
                .body(new ByteArrayResource(fileToView.getFileData()));

    }


    @GetMapping("/result")
    public String getHomePage(Model model){
        return "result";
    }



    }
