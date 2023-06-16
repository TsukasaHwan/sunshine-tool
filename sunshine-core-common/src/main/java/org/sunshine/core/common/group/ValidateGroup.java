package org.sunshine.core.common.group;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

/**
 * @author Teamo
 * @since 2022/03/24
 */
public class ValidateGroup {
    /**
     * 更新校验组
     */
    public interface Update {
    }

    /**
     * 添加校验组
     */
    public interface Insert {
    }

    /**
     * 顺序校验组
     */
    @GroupSequence({VerifySeq.N0.class, VerifySeq.N1.class, VerifySeq.N2.class, VerifySeq.N3.class,
            VerifySeq.N4.class, VerifySeq.N5.class, VerifySeq.N6.class, VerifySeq.N7.class,
            VerifySeq.N8.class, VerifySeq.N9.class, Default.class})
    public interface VerifySeq {
        interface N0 {
        }

        interface N1 {
        }

        interface N2 {
        }

        interface N3 {
        }

        interface N4 {
        }

        interface N5 {
        }

        interface N6 {
        }

        interface N7 {
        }

        interface N8 {
        }

        interface N9 {
        }

    }
}
