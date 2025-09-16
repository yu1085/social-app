package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 会员服务协议页面
 */
@Composable
fun MembershipAgreementScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black
                )
            }
            
            Text(
                text = "会员服务协议",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            // 占位符，保持标题居中
            Spacer(modifier = Modifier.size(48.dp))
        }
        
        // 协议内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // 特别提示
            AgreementSection(
                title = "特别提示",
                content = """
                    知聊会员服务是有偿的增值服务，《会员服务协议》(以下或称"本协议")由您(以下或称"知聊用户")和知聊进行缔结，对双方具有同等法律效力。知聊建议您仔细阅读本协议的全部内容，特别是免除或者限制责任的条款、法律适用和争议解决条款，若您认为本协议中的条款可能会导致您的部分或全部权利或利益受损，您应拒绝使用知聊会员服务；请您务必确保您在已经理解、完全同意下列所有服务条款并完成付费的前提下，方能享受相关服务。一旦您在网络页面点击同意本协议或以其他方式表示接受本协议或实际使用知聊会员服务的，即表示您已充分阅读、理解并接受本协议的全部内容。阅读本协议的过程中，如果您不同意本协议或其中任何条款约定，您应立即停止会员服务开通程序。
                """.trimIndent()
            )
            
            // 一、总则
            AgreementSection(
                title = "一、总则",
                content = """
                    1、知聊是由杭州知聊信息技术有限公司创建、运营的移动社交产品。
                    
                    2、知聊用户是指完成全部注册流程，愿意接受知聊《用户协议》并在知聊相关使用规范的规定下使用知聊服务的注册用户。
                    
                    3、知聊会员服务是知聊为知聊会员提供的有偿增值服务，用户向知聊支付相关会员费用后，方可享受专门为会员提供的各项服务（具体服务以开通流程及会员中心展示为准）。
                    
                    4、知聊会员必须遵守知聊《用户协议》、本服务条款及知聊各项规范。
                """.trimIndent()
            )
            
            // 二、服务费用
            AgreementSection(
                title = "二、服务费用",
                content = """
                    1、知聊用户获取会员资格应支付会员服务费，服务费用标准以会员中心页面及、或会员资格支付页面显示价格为准，用户应通过一次性支付会员服务费的方式购买会员服务。知聊将根据不同的购买方式和数量，向用户提供不同幅度的优惠，具体优惠信息以会员中心页面及、或会员资格支付页上内容为准。
                    
                    2、知聊可以根据实际情况随时调整会员服务费用标准，经调整的会员服务费用标准将在会员支付页上显示，用户按调整后的服务费用标准支付后方可享有或延续会员资格。在调整日期前按原资费标准购买服务的用户，其会员资格及服务在资费调整前购买的期限内不受影响，且无需补充缴纳差额；在调整日期后进行支付，购买知聊会员服务的用户，应按照更新后的资费标准进行支付。
                """.trimIndent()
            )
            
            // 三、服务内容
            AgreementSection(
                title = "三、服务内容",
                content = """
                    1、知聊将为会员提供多种专享的增值服务，具体内容将在会员中心页面显示。
                    
                    2、为了向会员提供更好的服务，保障会员的服务体验，知聊将根据实际情况不时调整（包括优选汰换、升级迭代等）会员服务内容。调整后的服务内容将通过包括但不限于站内通知、短信通知、在会员中心页面显示等一种或多种方式进行通知和公告，请您予以关注。
                """.trimIndent()
            )
            
            // 四、服务的开通和终止
            AgreementSection(
                title = "四、服务的开通和终止",
                content = """
                    1、您完全同意本协议所有服务条款，并通过知聊平台提供的付费途径完成会员费用的支付后，即取得知聊会员资格。
                    
                    2、您知晓并确认个人资料发生变化时，会员应及时修改注册的个人资料，否则由此造成的会员权利不能全面有效行使或其他任何后果、责任由会员自行承担。
                    
                    3、会员资格有效期自知聊会员服务开通之时起算，具体期限可以登录知聊会员中心页面进行查看。
                    
                    4、会员资格有效期届满后，知聊将终止向该用户提供会员增值服务，且有效期不可中止及中断;用户可以通过另行交纳会员服务费用再次获得会员资格。
                    
                    5、您在获取知聊会员服务时，应当遵守法律法规、本协议约定及相关的知聊会员服务规则，不得侵犯第三方或知聊的合法权益。知聊用户不得通过以下任何方式为自己或他人开通本协议项下会员服务(或称"本服务")，否则知聊有权终止向用户提供会员服务且不予退还任何费用：
                    
                    1)以营利、经营、商业等非个人使用的目的为自己或他人开通本服务；
                    2)通过任何机器人软件、蜘蛛软件、爬虫软件、刷屏软件等任何程序、软件方式为自己或他人开通本服务；
                    3)通过任何不正当手段或以违反诚实信用原则的方式（如利用规则漏洞、利用系统漏洞、滥用员身份、黑色产业、投机等）为自己或他人开通本服务；
                    4)通过非知聊指定的方式（包括未经知聊允许通过借用、租用、售卖、分享、转让等方式）为自己或他人开通本服务；
                    5)通过侵犯知聊或他人合法权益的方式为自己或他人开通本服务；
                    6)使用虚假信息、冒用他人信息等方式注册知聊账号；
                    7）通过利用或破坏知聊会员服务规则的方式；
                    8）通过其他违反相关法律、行政法规、国家政策等的方式为自己或他人开通本服务。
                    
                    知聊在此声明:任何未经知聊明示授权而售卖、租借或以类似方式转让知聊会员资格的行为属于侵权行为，知聊有权依法追究其法律责任。
                """.trimIndent()
            )
            
            // 五、会员的权利和义务
            AgreementSection(
                title = "五、会员的权利和义务",
                content = """
                    1、知聊会员平等享有会员的权利，同时应遵守知聊的各项规范、规则，包括但不限于本服务条款、《用户协议》以及平台其他行为规范等。
                    
                    2、因违反本服务条款、《用户协议》以及平台其他行为规范等而导致会员服务被暂停及、或终止使用的相关损失，由用户自行承担。
                    
                    3、会员在会员资格有效期内可以享受知聊会员的各项增值服务，会员资格有效期不因您未使用情况而延长。
                    
                    4、会员服务系网络商品和虚拟商品，会员费用是您所购买的会员服务所对应的网络商品价格，而非预付款或者存款、定金、储蓄卡等性质，会员服务一经开通后不可退款。您充分知悉并理解，作为您所支付的会员服务费用的服务对价，会员一经开通进入当期服务有效期，知聊基于会员体系所投入、打造的整套会员软件产品即时交付生效，知聊为此所付出的整体成本和努力不可拆分，故而知聊不接受任何原因（包括但不限于未实际使用、知聊提供的会员权益与期待存在差别、会员权益调整等理由）申请退款。如会员在会员资格有效期内主动终止或取消会员及、或用户资格的，则该会员剩余会员资格有效期对应的服务费用将不予退还，且无法恢复。
                    
                    5、会员自行承担在知聊中传送、发布信息及使用知聊免费服务或收费服务的法律责任，会员使用知聊服务，包括免费服务与收费服务的行为，均应遵守各项法律法规、规章、规范性文件(以下简称"法律法规")。
                    
                    6、知聊会员应确保注册资料的真实性，否则由知聊会员自行承担相应的责任。
                    
                    7、除双方另有约定外，您仅可出于个人、非商业的目的使用会员服务，并且该服务存在使用期限。您不得利用会员权益进行盈利或者非法获利，不以任何形式转让或者转移您所享有的会员服务或者权益，不以任何方式将会员服务或者权益借给他人使用。
                    
                    8、您充分理解并知悉，如若知聊提供的会员权益中包含三方联名合作权益的，则实际提供相应权益、商品、功能或服务的主体为第三方。三方作为该等权益的实际提供者，请您理解就该等权益相关服务约定及、或规则系由您与该第三方达成，知聊无法对三方权益的稳定性、持续性、有效性作出任何承诺与保证。因您使用三方内容产生的争议，您可直接与第三方协商解决。
                """.trimIndent()
            )
            
            // 六、知聊的权利和义务
            AgreementSection(
                title = "六、知聊的权利和义务",
                content = """
                    1、知聊有义务保证遵守本服务条款、知聊《用户协议》及各项规范、规则的会员正常使用会员服务，当出现技术故障影响会员服务的正常提供时，知聊应尽快排除故障，但因互联网服务的特殊性，员服务期限中包含知聊解决故障、服务器维修、调整、升级等或因第三方侵权处理所需用的合理时间，知聊并不因此延长会员服务期限，但知聊会尽可能将影响降至最低。
                    
                    2、知聊在未经会员授权时，不得公开、编辑或透露会员注册及保存的非公开信息，但法律法规明确规定及《用户协议》、《隐私政策》约定的情形除外。
                    
                    3、知聊将通过知聊系统通知、短信通知、邮箱通知、平台公示等方式对会员进行通知和公告。
                    
                    4、因不可抗力造成会员服务提供的中断或其他缺陷，知聊不承担任何责任，但将尽力减少因此给会员造成的损失和影响。您理解平台将尽合理努力确保服务及其所涉技术及信息安全、有效、准确、可靠，但受限于现有技术及平台有限的服务能力，对于非因平台经营者原因出现的任何履行障碍、瑕疵、延后或内容变更等情形，您理解平台无需承担任何责任。
                    
                    5、因知聊用户自身原因造成的知聊账号密码泄露，知聊不承担责任，同时为避免知聊账号被盗用后滥用而引起法律风险，知聊可以在用户举报该情形后采取一切措施避免第三方对用户的会员账号实施操作行为，而不论该第三方基于何种目的、获取方式是否善意。
                    
                    6、如知聊用户在使用知聊免费服务或收费服务的过程中，故意进行有损知聊平台、其他知聊用户合法权益的行为，或存在违反《用户协议》约定的相关情形，或存在重大过失并给知聊造成严重影响的，知聊有权取消该知聊用户的会员资格或中断一项或多项会员服务而无须给予任何补偿，并保留追究该知聊用户法律责任的权利。
                    
                    7、如知聊用户在使用知聊免费服务或收费服务的过程中，存在违反法律法规的行为，知聊有权取消该知聊用户的会员资格而无须给予任何补偿，且该知聊用户须自行承担全部责任。
                """.trimIndent()
            )
            
            // 七、其他
            AgreementSection(
                title = "七、其他",
                content = """
                    1、知聊不时发布的关于会员服务的规范、规则等，是本服务条款不可分给的一部分。
                    
                    2、本服务条款的解释，效力及纠纷的解决，适用中华人民共和国大陆法律。若知聊用户和知聊之间发生任何纠纷或争议，首先应友好协商解决，协商不成的，知聊用户同意将纠纷或争议提交知聊住所地有管辖权的人民法院管辖。
                    
                    3、本服务条款的版权由知聊所有，知聊保留合理解释和修改的权利。
                    
                    4、本协议的标题仅为方便及阅读而设，并不影响正文中任何条款的含义或解释。
                    
                    5、依照知聊发展的不同阶段，随着社区管理经验的不断丰富，出于维护知聊用户秩序的目的，知聊有权变更本协议内容，不断完善本协议服务条款。一旦本协议内容发生变更的，知聊将在相应页面、站内信或以其他合理方式进行通知，请您仔细阅读。如果您不同意变更的内容的，您可以选择停止使用知聊会员服务。如您继续使用知聊会员服务的，则视为您已经同意变更的全部内容。更新后的协议自文本更新之日起生效。
                """.trimIndent()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 协议章节组件
 */
@Composable
private fun AgreementSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // 标题
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 内容
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color(0xFF333333),
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
