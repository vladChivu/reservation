package carsrus.reservation.services;

import carsrus.reservation.dtos.MemberDTO;
import carsrus.reservation.dtos.MemberInput;
import carsrus.reservation.entities.Member;
import carsrus.reservation.exceptions.ResourceNotFoundException;
import carsrus.reservation.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MemberServiceImp implements MemberService{
    private MemberRepository memberRepository;

    public MemberServiceImp(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public static List<MemberDTO> getMemberDTOsFromMembers(Iterable<Member> all, boolean simple) {
        return StreamSupport.stream(all.spliterator(), false)
                .map(m -> simple ? new MemberDTO(null, m.getFirstName(), m.getLastName(), m.getEmail()) : new MemberDTO(m))
                .collect(Collectors.toList());
    }

    public static Member memberFromMemberInput(MemberInput mi) {
        return new Member(mi);
    }

    private String errorMessage(long id){
        return "Resource Not found with id = " + id;
    }
    private String errorMessageList() {
        return "No members found";
    }

    @Override
    public List<MemberDTO> getAllMembers(boolean simple) {
        if (memberRepository.findAll().isEmpty()) {
            throw new ResourceNotFoundException(errorMessageList());
        }
        return getMemberDTOsFromMembers(memberRepository.findAll(),simple);
    }

    @Override
    public MemberDTO addMember(MemberInput memberInput) {
        Member memberToAdd = memberFromMemberInput(memberInput);
        return new MemberDTO(memberRepository.save(memberToAdd));
    }

    @Override
    public MemberDTO findMemberDTOById(int id) {
        return new MemberDTO(memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(errorMessage(id))));
    }

    @Override
    public Member findMemberById(int id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(errorMessage(id)));
    }

    @Override
    public MemberDTO saveEditedCustomer(MemberDTO memberDTO) {
        int memberId = memberDTO.getId();
        Member memberInDB = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(errorMessage(memberId)));
        String membersFirstName = memberDTO.getFirstName();
        String membersLastName = memberDTO.getLastName();
        String membersEmail = memberDTO.getEmail();
        String membersStreet = memberDTO.getStreetName();
        String membersCity = memberDTO.getCity();
        String membersZipcode = memberDTO.getZipcode();
        if (membersFirstName != null) {
            memberInDB.setFirstName(membersFirstName);
        }
        if (membersLastName != null) {
            memberInDB.setLastName(membersLastName);
        }
        if (membersEmail != null) {
            memberInDB.setEmail(membersEmail);
        }
        if (membersStreet != null) {
            memberInDB.setStreet(membersStreet);
        }
        if (membersCity != null) {
            memberInDB.setCity(membersCity);
        }
        if (membersZipcode != null) {
            memberInDB.setZip(membersZipcode);
        }
        return new MemberDTO(memberRepository.save(memberInDB));
    }

    @Override
    public void removeMember(int memberId) {
        memberRepository.deleteById(memberId);
    }
}
