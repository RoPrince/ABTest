package alphabeta.model;

/***
 * Home for AlphaBean and BetaBean pair for a given feature.
 */
public class BeanPairVO {

    public BeanPairVO(String feature) {
        this.feature = feature;
    }

    private String feature;
    private BeanVO alphaBeanVO;
    private BeanVO betaBeanVO;

    public String getFeature() {
        return feature;
    }

    public BeanVO getAlphaBeanVO() {
        return alphaBeanVO;
    }

    public BeanVO getBetaBeanVO() {
        return betaBeanVO;
    }

    public void setAlphaBeanVO(BeanVO alphaBeanVO) {
        this.alphaBeanVO = alphaBeanVO;
    }

    public void setBetaBeanVO(BeanVO betaBeanVO) {
        this.betaBeanVO = betaBeanVO;
    }

    @Override
    public String toString() {
        return "alphaBetaBeanPairVO{" +
                "feature='" + feature + '\'' +
                ", alphaBeanVO=" + alphaBeanVO +
                ", betaBeanVO=" + betaBeanVO +
                '}';
    }
}
