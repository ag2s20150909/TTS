package me.ag2s.tts.view;

import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TextViewPagerTool implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = TextViewPagerTool.class.getSimpleName();

    public static final String test = "大奉京兆府，监牢。\n" +
            "\n" +
            "    许七安幽幽醒来，嗅到了空气中潮湿的腐臭味，令人轻微的不适，胃酸翻涌。\n" +
            "\n" +
            "    这扑面而来的臭味是怎么回事，家里的二哈又跑床上拉屎来了....根据熏人程度，怕不是在我头顶拉的....\n" +
            "\n" +
            "    许七安家里养了一条狗，品种哈士奇，俗称二哈。\n" +
            "\n" +
            "    北漂了十年，孤孤单单的，这人啊，寂寞久了，难免会想养条狗里慰藉和消遣....不是肉体上。\n" +
            "\n" +
            "    睁开眼，看了下周遭，许七安懵了一下。\n" +
            "\n" +
            "    石块垒砌的墙壁，三个碗口大的方块窗，他躺在冰凉的破烂草席上，阳光透过方块窗照射在他胸口，光束中尘糜浮动。\n" +
            "\n" +
            "    我在哪？\n" +
            "\n" +
            "    许七安在怀疑人生般的迷茫中沉思片刻，然后他真的怀疑人生了。\n" +
            "\n" +
            "    我穿越了....\n" +
            "\n" +
            "    狂潮般的记忆汹涌而来，根本不给他反应的机会，强势插入大脑，并快速流动。\n" +
            "\n" +
            "    许七安，字宁宴，大奉王朝京兆府下辖长乐县衙的一名捕快。月俸二两银子一石米。\n" +
            "\n" +
            "    父亲是老卒，死于十九年前的‘山海战役’，随后，母亲也因病去世......想到这里，许七安稍稍有些欣慰。\n" +
            "\n" +
            "    众所周知，父母双亡的人都不简单。\n" +
            "\n" +
            "    “没想到重活了，还是逃不掉当警察的宿命？”许七安有些牙疼。\n" +
            "\n" +
            "    他前世是警校毕业，成功进入体制，捧起了金饭碗。\n" +
            "\n" +
            "    可是，许七安虽然走了父母替他选择的道路，他的心却不在人民公仆这个职业上。\n" +
            "\n" +
            "    他喜欢无拘无束，喜欢自由，喜欢纸醉金迷，喜欢季羡林在日记本里的一句话：——\n" +
            "\n" +
            "    于是悍然辞职，下海经商。\n" +
            "\n" +
            "    “可我为什么会在监狱里？”\n" +
            "\n" +
            "    他努力消化着记忆，很快就明白自己眼下的处境。\n" +
            "\n" +
            "    许七安自幼被二叔养大，因为常年习武，每年要吃掉一百多两银子，因此被婶婶不喜。\n" +
            "\n" +
            "    18岁修炼到炼精巅峰后，便停滞不前，迫于婶婶的压力，他搬离许宅独自居住。\n" +
            "\n" +
            "    通过叔叔的关系，在衙门里混了个捕快的差事，原本日子过的不错，谁想到.....\n" +
            "\n" +
            "    三天前，那位在御刀卫当差的七品绿袍二叔，护送一批税银到户部，途中出了意外，税银丢失。\n" +
            "\n" +
            "    整整十五万两白银。\n" +
            "\n" +
            "    朝野震动，圣上勃然大怒，亲自下令，许平志于五日后斩首，三族亲属连坐，男丁发配边疆，女眷送入教坊司。\n" +
            "\n" +
            "    作为许平志的亲侄儿，他被解除了捕快职务，打入京兆府大牢。\n" +
            "\n" +
            "    两天！\n" +
            "\n" +
            "    再有两天时间，他就要被流放到凄苦荒凉的边陲之地，在劳碌中度过下半辈子。\n" +
            "\n" +
            "    “开局就是地狱模式啊....”许七安脊背发凉，心跟着凉了半截。\n" +
            "\n" +
            "    这个世界处在封建王朝统治的状态，没有人权的，边陲是什么地方？\n" +
            "\n" +
            "    荒凉，气候恶劣，大部分被发配边境的犯人，都活不过十年。而更多的人，还没到边陲就因为各种意外、疾病，死于途中。\n" +
            "\n" +
            "    想到这里，许七安头皮一炸，寒意森森。\n" +
            "\n" +
            "    “系统？”\n" +
            "\n" +
            "    沉默了片刻，寂静的监牢里响起许七安的试探声。\n" +
            "\n" +
            "    系统不搭理他。\n" +
            "\n" +
            "    “系统....系统爸爸，你出来啊。”许七安声音透着急切。\n" +
            "\n" +
            "    寂静无声。\n" +
            "\n" +
            "    没有系统，竟然没有系统！\n" +
            "\n" +
            "    这意味着他几乎没办法改变现状，两天后，他就要戴上镣铐和枷锁，被送往边陲，以他的体魄，应该不会死于途中。\n" +
            "\n" +
            "    但这并不是好处，在充当工具人的生涯里被压榨劳动力，最后死去.....\n" +
            "\n" +
            "    太可怕，太可怕了！\n" +
            "\n" +
            "    许七安对穿越古代这件事的美好幻想，如泡沫般破碎，有的只有焦虑和恐惧。\n" +
            "\n" +
            "    “我必须想办法自救，我不能就这样狗带。”\n" +
            "\n" +
            "    许七安在狭小的监牢里踱步打转，像是热锅上的蚂蚁，像是掉落陷阱的野兽，苦思对策。\n" +
            "\n" +
            "    我是炼精巅峰，身体素质强的吓人.....但在这个世界属于不屈白银，越狱是不可能的.....\n" +
            "\n" +
            "    靠宗族和朋友？\n" +
            "\n" +
            "    许家并非大族，族人分散各地，而整整十五万两的税银被劫，谁敢在这个节骨眼上求情？\n" +
            "\n" +
            "    根据大奉律法，将功补过，便可免除死罪！\n" +
            "\n" +
            "    除非找回银子....\n" +
            "\n" +
            "    许七安的眼睛猛的亮起，像极了濒临溺毙的人抓住了救命稻草。\n" +
            "\n" +
            "    他是正儿八经的警校毕业，理论知识丰富，逻辑清晰，推理能力极强，又阅读过无数的案例。\n" +
            "\n" +
            "    或许可以试着从破案这方面入手，追回银子，戴罪立功。\n" +
            "\n" +
            "    但随后，他眼里的光芒黯淡。\n" +
            "\n" +
            "    想要破案，首先要看卷宗，明白案件的详细经过。之后才是调查、破案。\n" +
            "\n" +
            "    如今他深陷大牢，叫天天不应叫地地不灵，两天后就送去边陲了！\n" +
            "\n" +
            "    无解！\n" +
            "\n" +
            "    许七安一屁股坐在地上，双目失神。\n" +
            "\n" +
            "    他昨儿在酒吧喝的伶仃大醉，醒来就在监狱里，想来可能是酒精中毒死掉了才穿越吧。\n" +
            "\n" +
            "    老天爷赏赐了穿越的机会，不是让他重活，是觉得他死的太轻松了？\n" +
            "\n" +
            "    在古代，发配是仅次于死刑的重刑。\n" +
            "\n" +
            "    上辈子虽然被社会毒打，好歹活在一个太平盛世，你说重生多好啊，二话不说，偷了父母的积蓄就去买房子。\n" +
            "\n" +
            "    然后配合老妈，把爱炒股的老爹的手打断，让他当不成韭菜。\n" +
            "\n" +
            "    这时，幽暗走廊的尽头传来锁链划动的声音，应该是门打开了。\n" +
            "\n" +
            "    继而传来脚步声。\n" +
            "\n" +
            "    一名狱卒领着一位神容憔悴的俊俏书生，在许七安的牢门前停下。\n" +
            "\n" +
            "    狱卒看了书生一眼：“半柱香时间。”\n" +
            "\n" +
            "    书生朝狱卒拱手作揖，目送狱卒离开后，他转过身来正面对着许七安。\n" +
            "\n" +
            "    书生穿着月白色的袍子，乌黑的长发束在玉簪上，模样甚是俊俏，剑眉星目，嘴唇很薄。\n" +
            "\n" +
            "    许七安脑海里浮现此人的相关记忆。\n" +
            "\n" +
            "    许家二郎，许新年。\n" +
            "\n" +
            "    二叔的亲儿子，许七安的堂弟，今年秋闱中举。\n" +
            "\n" +
            "    许新年平静的直视着他：“押送你去边陲的士卒收了我三百两，这是我们家仅剩的银子了，你安心的去，途中不会有意外的。”\n" +
            "\n" +
            "    “那你呢？”许七安鬼使神差的说出这句话，他记得原主和这位堂弟的关系并不好。\n" +
            "\n" +
            "    因为婶婶讨厌他的关系，许家除了二叔，其他人并不怎么待见许七安。至少堂弟堂妹不会表现的与他太过亲近。\n" +
            "\n" +
            "    除此之外，在原主的记忆里，这位堂弟还是个擅长口吐芬芳的嘴强王者。\n" +
            "\n" +
            "    许新年不耐烦道：“我已被革除功名，但有书院师长护着，不需要发配。管好你自己就行了。去了边陲，收敛脾气，能活一年是一年。”\n" +
            "\n" +
            "    许新年在京都赫赫有名的白鹿书院求学，颇受重视，又是新晋举人。因此，二叔出事后，他没有被下狱，但不允许离开京都，多天来一直各方奔走。\n" +
            "\n" +
            "    许七安沉默了，他不觉得许新年会比自己更好，恐怕不只是革除功名，还得入贱籍，子子孙孙不得科举，不得翻身。\n" +
            "\n" +
            "    且，两天后，许家女眷会被送入教坊司，受到凌辱。\n" +
            "\n" +
            "    许新年是读书人，他如何还有脸在京城活下去？或许被发配边疆才是更好的选择。\n" +
            "\n" +
            "    许七安心里一动，往前扑了几步，双手扣住铁栅栏：“你想自尽？！”\n" +
            "\n" +
            "    不受控制的，心里涌起了悲伤.....我明明都不认识他。\n" +
            "\n" +
            "    许新年面无表情的拂袖道：“与汝何干。”\n" +
            "\n" +
            "    顿了顿，他目光微微下移几寸，不与堂哥对视，神色转为柔和：“活下去。”\n" +
            "\n" +
            "    说罢，他决然的踏步离开！\n" +
            "\n" +
            "    “等等！”许七安手伸出栅栏，抓住他的衣袖。\n" +
            "\n" +
            "    许新年顿住，沉默的看着他。\n" +
            "\n" +
            "    “你能弄到卷宗吗？税银丢失案的卷宗。”";
    private final TextView tv;
    private final String text;
    private RePageListener listener;
    //private int start = 0;
    private int offset = 0;
    List<String> pages;

    public TextViewPagerTool(TextView tv, String text) {
        this.tv = tv;
        if (text == null) {
            this.text = tv.getText().toString();
        } else {
            this.text = text;
        }

        tv.setText(text);
        pages = new ArrayList<>();
    }

    @Override
    public void onGlobalLayout() {

        int start = 0;// tv.getLayout().getLineStart(0);
        int end = getCharNum();


        if (offset < text.length()) {
            Log.d(TAG, "Start:" + start + "End:" + end + "Offset:" + offset + "text.length" + text.length());
            String displayed = text.substring(offset, end + offset);
            pages.add(displayed);
            if (listener != null) {
                listener.onPage(displayed, pages.size());

            }
            offset += end;

            tv.setText(text.substring(offset));
            start();
            if (offset >= text.length()) {
                if (listener != null) {

                    //tv.setText(pages.get(0));
                    tv.setVisibility(View.VISIBLE);
                    listener.onFinished(text, pages, pages.size());
                }
            }
        }
//        //处理最后一行
//        if (offset < text.length()) {
//            String temp = text.substring(offset);
//            pages.add(temp);
//        }

        //listener.onPage(text, pages.size());


    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        return tv.getLayout().getLineEnd(getLineNum());
    }


    /**
     * 获取当前页总行数
     */
    private int getLineNum() {
        Layout layout = tv.getLayout();
        int topOfLastLine = tv.getHeight() - tv.getPaddingTop() - tv.getPaddingBottom() - tv.getLineHeight();
        return layout.getLineForVertical(topOfLastLine);
    }

    /**
     * 重新分页监听器
     */
    public interface RePageListener {
        void onPage(String txt, int page);

        void onFinished(String text, List<String> data, int page);
    }

    public TextViewPagerTool setListener(RePageListener listener) {
        this.listener = listener;
        return this;
    }

    public void start() {
        tv.setVisibility(View.INVISIBLE);
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(this);
    }
}
