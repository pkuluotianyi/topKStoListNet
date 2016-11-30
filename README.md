# topKStoListNet
<p>This is implementation of Stochastic ListNet(A listwise learning to rank algorithm) top-k algorithm. We extend ListNet's fast computing algorithm - Top k probabilities algorithm to k > 2
and adopt three new sampling approaches to speed up training time. Results showed that our approach reduce the training complexity and get better p@1 performance.</p>

<p><a href="http://www.aclweb.org/anthology/D/D15/D15-1079.pdf">Stochastic Top-k ListNet (EMNLP 2015 LONG ORAL PAPER)</a></p>

<p>The definitions of parameters are as following:</p>
<p>	-train               The location of your tarining set file. e.g. C:\MQ2008\Fold1\train.txt</p>
<p>	-validate      		 The location of your tarining set file. e.g. C:\MQ2008\Fold1\vali.txt</p>
<p>	-test                The location of your tarining set file. e.g. C:\MQ2008\Fold1\test.txt</p>
<p>	-metric2t            Metric to optimize on the training data. We surpport MAP, NDCG@k and P@k.</p>
<p>	-metric2T            Metric to evaluate on the test data. We surpport MAP, NDCG@k and P@k.</p>
<p>	-iterations          Number of iterations.</p>
<p>	-k                   Parameter of Top k probabilities algorithm.</p>
<p>	-learningRate        The learning rate e.g 1.0E-5. </p>
<p>	-save          	     Saving the model file</p>
<p>	-isOrNotResampling   Whether we adopt re-sampling or not.</p>
<p>	-samplingMethods     Category of stochastic method. e.g. UDS, FDS and ADS.</p>
<p>	-numberOfsampling    The number of samplings.</p>

<p>The example is as following:</p>
<p>java stoListNet.jar -train C:\MQ2008\Fold1\train.txt -test C:\MQ2008\Fold1\test.txt -validate C:\MQ2008\Fold1\vali.txt -metric2t P@1 -metric2T P@1 -save mymodel.txt -iterations 20 -learningRate 1.0E-3 -k 1 -isOrNotResampling no -samplingMethods ADS -numberOfsampling 5</p>
