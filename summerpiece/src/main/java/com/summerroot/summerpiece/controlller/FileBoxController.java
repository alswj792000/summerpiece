package com.summerroot.summerpiece.controlller;

import com.summerroot.summerpiece.domain.FileBox;
import com.summerroot.summerpiece.domain.FileBoxStatus;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.repository.FileBoxRepository;
import com.summerroot.summerpiece.service.FileBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FileBoxController {

    private final FileBoxService fileBoxService;
    private final FileBoxRepository fileBoxRepository;

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/fileBox/main")
    public String fileBoxMain(Model model,
                                @RequestParam(required = false, defaultValue = "0", value = "page") int page,
                                @RequestParam(required = false, defaultValue = "", value = "keyword") String keyword
                            ) {
        String fileBoxStatus = FileBoxStatus.Y.name();
        Page<FileBox> pList = fileBoxService.pageList(page, keyword, fileBoxStatus);

        int totalPage = pList.getTotalPages();

        model.addAttribute("fileList", pList.getContent());
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("pageNum", page);
        model.addAttribute("resultDataTotal", pList.getTotalElements());
        model.addAttribute("size", pList.getNumber());
        model.addAttribute("keyword", keyword);

        return "fileBox/fileBox";

    }

    @PostMapping("fileBox/main")
    public String uploadFile(@AuthenticationPrincipal Member member,
                             @RequestParam("files") List<MultipartFile> fList,
                             Model model,
                             @RequestParam(required = false, defaultValue = "0", value = "page") int page,
                             @RequestParam(required = false, defaultValue = "", value = "keyword") String keyword) throws IOException{

        for(MultipartFile multipartFile : fList){
            fileBoxService.saveFile(multipartFile, member);
        }
        String fileBoxStatus = FileBoxStatus.Y.name();
        Page<FileBox> pList = fileBoxService.pageList(page, keyword, fileBoxStatus);

        int totalPage = pList.getTotalPages();

        model.addAttribute("fileList", pList.getContent());
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("pageNum", page);
        model.addAttribute("resultDataTotal", pList.getTotalElements());
        model.addAttribute("size", pList.getNumber());
        model.addAttribute("keyword", keyword);


        return "fileBox/fileBox";
    }

    @GetMapping("/attach/{id}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long id, HttpServletResponse response) throws IOException {
        FileBox file = fileBoxRepository.findById(id).orElse(null);

        UrlResource resource = new UrlResource("file:" + file.getFilePath());

        String encodedFileName = UriUtils.encode(file.getFileOriginName(), StandardCharsets.UTF_8);

        // 파일 다운로드 대화상자가 뜨도록 하는 헤더 설정
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"" ;

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);

    }

    @ResponseBody
    @PostMapping("/deleteFile")
    public Map<Long, Object> deleteFile(@AuthenticationPrincipal Member member,
                                  @RequestBody Map<String, Object> allData
                                   ){
        Map<Long, Object> map = new HashMap<Long, Object>();

        log.info("fileIdArr={}", allData);

        Long mId = Long.valueOf(member.getId().toString());
        allData.get("fileUploadMember").toString();
        Long fileUploadMember = Long.valueOf(String.valueOf(allData.get("fileUploadMember")));

        if(!mId.equals(fileUploadMember)){
            return (Map<Long, Object>) null;
        } else{
            fileBoxService.deleteFile((List<String>) allData.get("fileIdArr"), mId);
            return map;
        }
    }
}