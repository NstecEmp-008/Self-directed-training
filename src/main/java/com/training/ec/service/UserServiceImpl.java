package com.training.ec.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.training.ec.entity.Account;
import com.training.ec.entity.AccountRole;
import com.training.ec.entity.Wallet;
import com.training.ec.form.UserRegistrerform;
import com.training.ec.repository.UserRepository;
import com.training.ec.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

/**
 * ユーザー登録・管理に関するビジネスロジックを担当するサービスクラスです。
 *
 * @Service アノテーションは、このクラスがSpringのサービスコンポーネントであることを示します。
 * Springはこれを自動的に管理（DIコンテナに登録）し、他のコンポーネントから利用できるようにします。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // ロガーを定義。ログを出力するために使用します。
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    // UserRepositoryを自動的に注入します。データベース操作に使用します。
    @Autowired
    private UserRepository userRepository;

    // PasswordEncoderを自動的に注入します。パスワードのハッシュ化に使用します。
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final WalletRepository walletRepository;

    @Value("${app.initial.cash:1000000}")
    private BigDecimal initialCash; // 初期現金額をプロパティから取得。デフォルトは100,000円。

    /**
     * ユーザー登録処理を行います。
     *
     * @param form ユーザー登録フォームの入力値を含むオブジェクトです。
     * @Transactional アノテーションは、このメソッドの処理全体が単一のデータベーストランザクションとして扱われることを示します。
     * メソッドの途中で例外が発生した場合、データベースへの変更は全て自動的にキャンセル（ロールバック）されます。
     */
    @Override
    @Transactional
    public void register(UserRegistrerform form) {
        // パスワードと確認用パスワードが一致するかをチェックします。
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("パスワードが一致しません");
        }

        // ユーザー名が既にデータベースに存在するかチェックします。
        Account existing = userRepository.selectByUserName(form.getUsername());
        if (existing != null) {
            throw new IllegalArgumentException("このユーザー名は既に登録されています");
        }

        // 入力されたパスワードをハッシュ化（暗号化）します。
        String hashedPassword = passwordEncoder.encode(form.getPassword());

        logger.info("データベースから'USER'ロールを取得しようとしています。");

        // データベースから'USER'ロールを取得します。
        AccountRole userRole = userRepository.selectRoleByName("ROLE_USER");
        // もし'USER'ロールが存在しない場合、致命的なエラーとして例外をスローします。
        if (userRole == null) {
            logger.error("データベースで'ROLE_USER'ロールが見つかりませんでした。'account_role'テーブルと'data.sql'ファイルを確認してください。");
            throw new IllegalStateException("ROLE_USERロールがDBに存在しません");
        }
        logger.info("ID: {} の'USER'ロールを正常に取得しました。", userRole.getRoleId());
        // List<AccountRole> userRole = userRepository.selectRoleByName2("ROLE_USER");
        // for (AccountRole role : userRole) {
        //     logger.info("ID: {} の'USER'ロールを正常に取得しました。", role.getRoleId());
        //     logger.info("Name: {} の'USER'ロールを正常に取得しました。", role.getRoleName());
        // }
        // ユーザー登録のためのAccountエンティティを作成し、情報を設定します。
        Account account = new Account();
        account.setUserName(form.getUsername());
        account.setPassword(hashedPassword);
        account.setRole(userRole);

        // UserRepositoryを介してデータベースに新しいアカウント情報を保存します。
        userRepository.insertAccount(account);

      
        // 7. INSERT後に自動採番された userId を取得
        Integer newUserId = account.getUserId();
        if (newUserId == null) {
            throw new IllegalStateException("ユーザー登録後に userId が取得できませんでした");
        }

        // 8. Wallet を初期化
        Wallet wallet = new Wallet();
        wallet.setUserId(newUserId);
        wallet.setBalance(initialCash);
        walletRepository.insertWallet(wallet);

        logger.info("新しいユーザー「{}」が登録され、初期残高 {} 円を付与しました。",
                    form.getUsername(), initialCash);
    }

    /**
     * 指定されたユーザーIDのロールを更新するメソッドです。
     *
     * @param userId 更新するユーザーのID
     * @param newRoleName 新しいロール名
     */
    @Override
    @Transactional
    public void updateUserRole(Integer userId, String newRoleName) {
        logger.info("ユーザーID: {} のロールを {} に更新しようとしています。", userId, newRoleName);

        // 1. 新しいロール名からデータベースのロール情報を取得します。
        AccountRole newRole = userRepository.selectRoleByName(newRoleName);
        if (newRole == null) {
            throw new IllegalArgumentException("指定されたロール名が見つかりません: " + newRoleName);
        }

        // 2. UserRepositoryを介してデータベースでロールIDを更新します。
        // このメソッドは、MyBatisのMapperファイルにも追加する必要があります。
        userRepository.updateUserRole(userId, newRole.getRoleId());

        logger.info("ユーザーID: {} のロールが正常に更新されました。", userId);
    }
}
