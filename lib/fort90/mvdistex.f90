!
      PROGRAM TSTMV
!
!     Test program for MVDIST
!
      USE PRECISION_MODEL
      USE MVSTAT
      IMPLICIT NONE
      INTEGER :: I, IVLS, INF
      INTEGER,                        PARAMETER :: IP = 0, MX = 100000
      REAL(KIND=STND),                PARAMETER :: ABSEPS = 1E-3_STND
      REAL(KIND=STND)                           :: DI
!
      INTEGER,                        PARAMETER :: N1 = 3, M1 = 3
      REAL(KIND=STND), DIMENSION(N1,N1)         :: COV1
      REAL(KIND=STND), DIMENSION(M1,N1)         :: CNS1
      INTEGER,                        PARAMETER :: NU1 =  5
      REAL(KIND=STND), DIMENSION(M1), PARAMETER :: LW1 = (/ -3, -2, -1 /)
      REAL(KIND=STND), DIMENSION(M1), PARAMETER :: UP1 = 2
      REAL(KIND=STND), DIMENSION(M1), PARAMETER :: DL1 = 0
      INTEGER,         DIMENSION(M1), PARAMETER :: FN1 = 2
!
      INTEGER,                        PARAMETER :: N2 = 3, M2 = 3 
      REAL(KIND=STND), DIMENSION(N2,N2)         :: COV2
      REAL(KIND=STND), DIMENSION(M2,N2)         :: CNS2
      INTEGER,                        PARAMETER :: NU2 =  5
      REAL(KIND=STND), DIMENSION(M2), PARAMETER :: LW2 = -3.115E0_STND
      REAL(KIND=STND), DIMENSION(M2), PARAMETER :: UP2 =  3.115E0_STND
      REAL(KIND=STND), DIMENSION(M1), PARAMETER :: DL2 = 0
      INTEGER,         DIMENSION(M1), PARAMETER :: FN2 = 2
!
      INTEGER,                        PARAMETER :: N3 = 4, M3 = 6
      REAL(KIND=STND), DIMENSION(N3,N3)         :: COV3
      REAL(KIND=STND), DIMENSION(M3,N3)         :: CNS3
      INTEGER,                        PARAMETER :: NU3 =  100
      REAL(KIND=STND), DIMENSION(M3), PARAMETER :: LW3 = -2.601E0_STND
      REAL(KIND=STND), DIMENSION(M3), PARAMETER :: UP3 =  2.601E0_STND
      REAL(KIND=STND), DIMENSION(M3), PARAMETER :: DL3 =  0
      INTEGER,         DIMENSION(M3), PARAMETER :: FN3 =  2
!
      INTEGER,                        PARAMETER :: N4 = 8, M4 = 21
      REAL(KIND=STND), DIMENSION(N4,N4)         :: COV4
      REAL(KIND=STND), DIMENSION(M4,N4)         :: CNS4
      INTEGER,                        PARAMETER :: NU4 =  86
      REAL(KIND=STND), DIMENSION(M4), PARAMETER :: LW4 = -2.976E0_STND
      REAL(KIND=STND), DIMENSION(M4), PARAMETER :: UP4 =  2.976E0_STND
      REAL(KIND=STND), DIMENSION(M4), PARAMETER :: DL4 =  0
      INTEGER,         DIMENSION(M4), PARAMETER :: FN4 =  2
      REAL(KIND=STND), DIMENSION(M4), PARAMETER :: LD4 = -1
      REAL(KIND=STND), DIMENSION(M4), PARAMETER :: UD4 =  1
!
      INTEGER,                        PARAMETER :: N5 = 4, M5 = N5
      REAL(KIND=STND), DIMENSION(N5,N5)         :: COV5
      REAL(KIND=STND), DIMENSION(M5,N5)         :: CNS5
      INTEGER,                        PARAMETER :: NU5 =  0
      REAL(KIND=STND), DIMENSION(M5), PARAMETER :: LW5 =  0
      REAL(KIND=STND), DIMENSION(M5), PARAMETER :: UP5 =  0
      REAL(KIND=STND), DIMENSION(M5), PARAMETER :: DL5 =  (/-19, -14, -10, -5/)
      INTEGER,         DIMENSION(M5), PARAMETER :: FN5 =  0
!
      INTEGER,                        PARAMETER :: N6 = 5, M6 = N6
      REAL(KIND=STND), DIMENSION(N6,N6)         :: COV6
      REAL(KIND=STND), DIMENSION(M6,N6)         :: CNS6
      INTEGER,                        PARAMETER :: NU6 =  0
      REAL(KIND=STND), DIMENSION(M6), PARAMETER :: LW6 = (/ -41E0_STND,     &
                                  -35E0_STND,-3E0_STND,-16E0_STND,2.7E0_STND/)
      REAL(KIND=STND), DIMENSION(M6), PARAMETER :: UP6 = (/   1E0_STND,     &
                                  2E0_STND,  3E1_STND,  4E-1_STND, 5E1_STND /)
      REAL(KIND=STND), DIMENSION(M6), PARAMETER :: DL6 = (/ -19E0_STND,      &
                              -14E0_STND, -10E0_STND, -5E0_STND, 10E0_STND  /)
      INTEGER,         DIMENSION(M6), PARAMETER :: FN6 =  2
!
      INTEGER,                        PARAMETER :: N7 = 7, M7 = N7
      REAL(KIND=STND), DIMENSION(N7,N7)         :: COV7
      REAL(KIND=STND), DIMENSION(M7,N7)         :: CNS7
      INTEGER,                        PARAMETER :: NU7 =  0
      REAL(KIND=STND), DIMENSION(M7), PARAMETER :: LW7 = (/ -41,-35,-30,-10, &
                                                             -5, -6, -8 /)
      REAL(KIND=STND), DIMENSION(M7), PARAMETER :: UP7 = (/   1,  2,  3,  4, &
                                                             20, 40,  7 /)
      REAL(KIND=STND), DIMENSION(M7), PARAMETER :: DL7 = (/ -19E0_STND,      &
          -14E0_STND,-10E0_STND,-5E0_STND,10E0_STND,25E0_STND,-1.2E0_STND /)
      INTEGER,         DIMENSION(M7), PARAMETER :: FN7 =  2
!
      PRINT "(""            Test of MVDIST"")"
      PRINT "(12X, ""Requested Accuracy "",F8.5)", ABSEPS
!
      CNS1 = 0
      COV1 = 0
      DO I = 1, N1
         COV1(I,I) = 1
         CNS1(I,I) = 1
      END DO
      CALL MVPRNT( N1, COV1, NU1, M1, LW1, CNS1, UP1, FN1, DL1,               &
           MX, ABSEPS, "Diagonal Constraint and Covariance", IP )
      COV1(2,1) = -6E-1_STND
      COV1(3,1) =  12E0_STND/13
      COV1(3,2) = -56E0_STND/65   ! Singular Case
!
      DO I = 1, N1-1
         COV1(I:I,I+1:N1) = TRANSPOSE(COV1(I+1:N1,I:I))
      END DO
      CALL MVPRNT( N1, COV1, NU1, M1, LW1, CNS1, UP1, FN1, DL1,               &
           MX, ABSEPS, "    3-D Singular Case", IP )
!
      COV2 = COV1
      CALL ALLCOM( N2, CNS2 )
      DO I = 1, M2
         DI = SUM( MATMUL( CNS2(I:I,:), COV1 )*CNS2(I:I,:) )
         CNS2(I,:) = CNS2(I,:)/SQRT(DI)
      END DO
      CALL MVPRNT( N2, COV1, NU2, M2, LW2, CNS2, UP2, FN2, DL2,               &
           MX, ABSEPS, "3-D Singular All Pairs Comparison", IP )
!
      COV3 = 0
      COV3(1,1) = 1/12E0_STND
      COV3(2,2) = 1/28E0_STND
      COV3(3,3) = 1/44E0_STND
      COV3(4,4) = 1/20E0_STND
      CALL ALLCOM( N3, CNS3 )
      DO I = 1, M3
         DI = SUM( MATMUL( CNS3(I:I,:), COV3 )*CNS3(I:I,:) )
         CNS3(I,:) = CNS3(I,:)/SQRT(DI)
      END DO
      CALL MVPRNT( N3, COV3, NU3, M3, LW3, CNS3, UP3, FN3, DL3,              &
           MX, ABSEPS, "All Pairs Comparison with n's = (12,28,44,20)", IP )
!
      COV4(1,1  ) = 0.84356976624013
      COV4(2,1:2) = (/0.68824621656048,0.86786330158392/)
      COV4(3,1:3) = (/0.49129338783523,0.44105168666416,0.36746916488365/)
      COV4(4,1:4) = (/0.58231396647057,0.52276412310698,0.37316668206831,    &
                      0.52563558321164/)
      COV4(5,1:5) = (/0.47575489833279,0.42710222759763,0.30487999105473,    &
                      0.36136427089089,0.42023733219180/) 
      COV4(6,1:6) = (/0.36856901214714,0.33087761505587,0.23619161362343,    &
                      0.27995018614472,0.22872141150037,0.22481033874186/)  
      COV4(7,1:7) = (/0.89996323014080,0.80792925453018,0.57672718140463,    &
                      0.68357584467990,0.55848661583643,0.43266156799977,    &
                      1.11528646976173/)
      COV4(8,1:8) = (/-0.07521816574431,-0.06752604388895,-0.04820237012723, &
                      -0.05713269104994,-0.04667783908171,-0.03616148798425, &
                      -0.08829827918363, 0.00737989550699/)
      DO I = 1, N4-1
         COV4(I:I,I+1:N4) = TRANSPOSE(COV4(I+1:N4,I:I))
      END DO
      CALL ALLCOM( N4-1, CNS4 )
      DO I = 1, M4
         DI = SUM( MATMUL( CNS4(I:I,:), COV4 )*CNS4(I:I,:) )
         CNS4(I,:) = CNS4(I,:)/SQRT(DI)
      END DO
      CALL MVPRNT( N4, COV4, NU4, M4, LW4, CNS4, UP4, FN4, DL4,              &
           MX, ABSEPS, "Starch All Pairs Comparison ",IP )
!
!      Bansal, Perkins, Pistikopoulos JSCS 67(200), pp. 219-254 Examples
!
      COV5 = 0
      CNS5 = 0
      DO I = 1, N5
         CNS5(I,I) = 1
      END DO
      COV5(1,1  ) = 112.75875E0_STND
      COV5(2,1:2) = (/78.06375E0_STND,130.10625E0_STND/)
      COV5(3,1:3) = (/34.695E0_STND,-86.7375E0_STND,173.475E0_STND/)
      COV5(4,1:4) = (/0E0_STND,-34.695E0_STND,52.0425E0_STND,17.3475E0_STND/)
      CALL MVPRNT( N5, COV5, NU5, M5, LW5, CNS5, UP5, FN5, DL5,              &
           MX, ABSEPS, " BPP Example 1 ",IP )
!
      COV6 = 0
      CNS6 = 0
      DO I = 1, N6
         CNS6(I,I) = 1
      END DO
      COV6(1:4,1:4) = COV5(1:4,1:4) 
      COV6(5,1:5) = (/0E0_STND,-104.085E0_STND,156.1275E0_STND,52.0425E0_STND,&
                   158.9031E0_STND/)
      CALL MVPRNT( N6, COV6, NU6, M6, LW6, CNS6, UP6, FN6, DL6,              &
           MX, ABSEPS, " BPP Example 2 ",IP )
!
      COV7 = 0
      CNS7 = 0
      DO I = 1, N7
         CNS7(I,I) = 1
      END DO
      COV7(1:5,1:5) = COV6(1:5,1:5) 
      COV7(6,1:6) = (/0E0_STND,-173.475E0_STND,260.2125E0_STND,86.7375E0_STND,&
                   265.7637E0_STND,444.7899E0_STND/)
      COV7(7,1:7) = (/-149.18850_STND,-79.7985E0_STND,-69.39E0_STND,0E0_STND, &
                     0E0_STND,0E0_STND,302.5404E0_STND/)
      CALL MVPRNT( N7, COV7, NU7, M7, LW7, CNS7, UP7, FN7, DL7,              &
           MX, ABSEPS, " BPP Example 3 ",IP )
!
     END PROGRAM TSTMV
!
   SUBROUTINE ALLCOM( N, CONSTR )
!
!    Computes constraint for all pairs comparisons
! 
     USE PRECISION_MODEL
      IMPLICIT NONE
      INTEGER,                                 INTENT(IN)  :: N
      REAL(KIND=STND), DIMENSION(N*(N-1)/2,N), INTENT(OUT) :: CONSTR
!
      INTEGER I, J, IJ
      CONSTR = 0 
      IJ = 0
      DO  I = 1, N-1 
         DO J = I+1, N 
            IJ = IJ + 1 
            CONSTR(IJ,I) = -1 
            CONSTR(IJ,J) =  1
         END DO
      END DO
   END SUBROUTINE ALLCOM
!
   SUBROUTINE MVPRNT( N, COVRNC, NU, M, LOWER, CONSTR, UPPER, INFIN, DELTA,  &
                      MAXPTS, ABSEPS, LABEL, IP )
      USE PRECISION_MODEL
      USE MVSTAT
      IMPLICIT NONE
      INTEGER,                         INTENT(IN) :: N, NU, M, MAXPTS, IP
      REAL(KIND=STND), DIMENSION(M),   INTENT(IN) :: LOWER, UPPER, DELTA
      REAL(KIND=STND), DIMENSION(N,N), INTENT(IN) :: COVRNC
      REAL(KIND=STND), DIMENSION(M,N), INTENT(IN) :: CONSTR
      INTEGER,         DIMENSION(M),   INTENT(IN) :: INFIN
      CHARACTER(LEN=*),                INTENT(IN) :: LABEL
      REAL(KIND=STND),                 INTENT(IN) :: ABSEPS
!
      INTEGER                                     :: I, INF, IVLS
      REAL(KIND=STND)                             :: LWR, UPR, ALPHA, TALPHA
      REAL(KIND=STND)                             :: ERROR, VALUE
!
      PRINT "(/10X,A)", LABEL
      PRINT "(""           Number of Dimensions is "",I3)", N
      PRINT "(""          Number of Constraints is "",I3)", M
      PRINT "(""      Number of Degrees of Freedom is "",I3)", NU
      PRINT "(""     Maximum # of Function Evaluations is "",I7)", MAXPTS
      IF ( IP .GT. 0 ) THEN
         PRINT "(""    Lower  Upper     Constraints "")"
         DO I = 1, M
            IF ( INFIN(I) < 0 ) THEN 
               PRINT "(I2, ""  -00    00   "", 7F7.3)",  CONSTR(I,1:N)
            ELSE IF ( INFIN(I) == 0 ) THEN 
               PRINT "(I2, ""  -00  "", 10F7.3)", I, UPPER(I), CONSTR(I,1:N)
            ELSE IF ( INFIN(I) == 1 ) THEN 
               PRINT "(I2,F7.3,""  00   "",9F7.3)", I, LOWER(I), CONSTR(I,1:N)
            ELSE 
               PRINT "(I2, 11F7.3)", I, LOWER(I), UPPER(I), CONSTR(I,1:N)
            ENDIF
         END DO
         PRINT "(""     Lower Left Covariance Matrix "")"
         DO I = 1, N
            PRINT "(I2, 11F7.3)", I, COVRNC(I,1:I)
         END DO
      END IF
      CALL MVDIST( N, COVRNC, NU, M, LOWER, CONSTR, UPPER, INFIN, DELTA,      &
                   MAXPTS, ABSEPS, 0E0_STND, ERROR, VALUE, IVLS, INF )
      PRINT "(5X, ""Value(Error): "",F9.6,""("",F9.6,"")"")", VALUE, ERROR
      PRINT "(5X,""Evaluations(Inform): "",I8,""("",I2,"")"")", IVLS, INF
      CALL LWRUPR( N, COVRNC, NU, M,LOWER, CONSTR, UPPER, INFIN, DELTA,       &
                   1E0_STND, LWR, UPR )
      PRINT "(""  2-Bounds:"",2F9.6,"", average:"",F9.6)", LWR,UPR,(LWR+UPR)/2
      CALL LWUPRH( N, COVRNC, NU, M,LOWER, CONSTR, UPPER, INFIN, DELTA,       &
                   1E0_STND, LWR, UPR )
      PRINT "(""2-3-Bounds:"",2F9.6,"", average:"",F9.6)", LWR,UPR,(LWR+UPR)/2
      CALL LWUPRT( N, COVRNC, NU, M,LOWER, CONSTR, UPPER, INFIN, DELTA,       &
                   1E0_STND, LWR, UPR )
      PRINT "(""  3-Bounds:"",2F9.6,"", average:"",F9.6)", LWR,UPR,(LWR+UPR)/2
      ALPHA = 1E-1_STND
      DO I = 1, 2
         CALL MVCRIT( N, COVRNC, NU, M,LOWER-DELTA,CONSTR,UPPER-DELTA,INFIN,  &
                      ALPHA, 100*MAXPTS, ABSEPS, ERROR, TALPHA, IVLS, INF ) 
         PRINT "("" Alpha, T_alpha, Work, Inform: "", F6.2, F11.6, I10, I5)", &
                    ALPHA, TALPHA, IVLS, INF
         ALPHA = ALPHA/2
      END DO
   END SUBROUTINE MVPRNT


