package mg.haja.federationagricole.service;

import mg.haja.federationagricole.DTO.CreateMemberPayment;
import mg.haja.federationagricole.Entity.MemberPayment;
import mg.haja.federationagricole.repository.FinancialAccountRepository;
import mg.haja.federationagricole.repository.PaymentRepository;
import mg.haja.federationagricole.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final FinancialAccountRepository accountRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          TransactionRepository transactionRepository,
                          FinancialAccountRepository accountRepository) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<MemberPayment> create(String memberId, List<CreateMemberPayment> requests) {
        try {
            if (!paymentRepository.memberExists(memberId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
            }
            String collectivityId = paymentRepository.findCollectivityIdByMember(memberId);
            if (collectivityId == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member has no active collectivity");
            }

            List<MemberPayment> created = new ArrayList<>();
            for (CreateMemberPayment req : requests) {
                if (!paymentRepository.membershipFeeExists(req.getMembershipFeeIdentifier())) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership fee not found");
                }

                // 1. Save payment
                MemberPayment payment = paymentRepository.save(memberId, req);

                // 2. Update account balance
                accountRepository.updateBalance(req.getAccountCreditedIdentifier(), req.getAmount());

                // 3. Auto-create transaction
                transactionRepository.save(
                    collectivityId, memberId,
                    req.getAmount(), req.getPaymentMode(),
                    req.getAccountCreditedIdentifier()
                );

                created.add(payment);
            }
            return created;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
