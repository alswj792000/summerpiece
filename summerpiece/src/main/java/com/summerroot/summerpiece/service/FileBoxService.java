package com.summerroot.summerpiece.service;


import com.summerroot.summerpiece.domain.FileBox;
import com.summerroot.summerpiece.domain.FileBoxStatus;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.repository.FileBoxRepository;
import com.summerroot.summerpiece.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileBoxService {

    private final FileBoxRepository fileBoxRepository;
    private final MemberRepository memberRepository;
    @Value("${file.dir}")
    private String fileDir;

    @Transactional
    public Long saveFile(MultipartFile files, @AuthenticationPrincipal Member member) throws IOException{
        if(files.isEmpty()){
            return null;
        }
        // 파일 원래이름 추출
        String fileOriginName = files.getOriginalFilename();

        // 파일 이름으로 쓸  uuid 생성
        String uuid = UUID.randomUUID().toString();

        //확장자 추출
        String fileType = fileOriginName.substring(fileOriginName.lastIndexOf("."));

        //uuid 확장자 결합
        String fileSavedName = uuid + fileType;

        //파일을 불러올 때 사용할 파일 경로
        String filePath = fileDir + fileSavedName;

        // 파일 크기
//        Long fileSize = files.getSize();

        LocalDateTime fileUpdateDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS");
        String nowString = fileUpdateDate.format(dateTimeFormatter);

        LocalDateTime modifiedDate = LocalDateTime.now();

        FileBoxStatus status = FileBoxStatus.Y;

        //파일 엔티티생성
        FileBox file = FileBox.builder()
                .fileOriginName(fileOriginName)
                .filePath(filePath)
                .fileSavedName(fileSavedName)
//                .fileSize(fileSize)
                .fileUpdateDate(nowString)
                .modifiedDate(modifiedDate)
                .fileType(fileType)
                .fileUploadMember(member)
                .status(status)
                .build();

        // 실제로 로컬에 uuid를 파일명으로 저장
        files.transferTo(new File(filePath));

        // db에 파일정보 저장
        FileBox savedFile = fileBoxRepository.save(file);

        return savedFile.getId();
    }

    @Transactional
    public List<FileBox> findFileList() {
        return fileBoxRepository.findAll();
    }

    @Transactional
    public void deleteFile(List<String> fileIdArr, Long mId) {
        memberRepository.findOne(mId);

        for (int i = 0; i < fileIdArr.size(); i++) {
            String fileIdx = fileIdArr.get(i);
            Optional<FileBox> optionalFileBox = fileBoxRepository.findById(Long.parseLong(fileIdx));
            if (optionalFileBox.isPresent()) {
                FileBox fileBox = optionalFileBox.get();
                fileBox.deleteFile();
                fileBoxRepository.save(fileBox);
            } else {
                throw new NullPointerException();
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<FileBox> pageList(int page, String keyword, String status){
        FileBoxStatus fileBoxStatus = FileBoxStatus.valueOf(status);
        if(keyword.isEmpty()){
            return fileBoxRepository.findAllByStatus(PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")), fileBoxStatus);
        } else{
            return fileBoxRepository.findByFileOriginNameContainingIgnoreCaseAndStatus(PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")), keyword, fileBoxStatus);
        }
    }

}
