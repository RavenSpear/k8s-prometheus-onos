<template>
        <div>
                <div id="mountNode"></div>
        </div>
</template>

<script>
import G6 from '@antv/g6';
export default {
        name: "dashboard",
        data() {
                return {
                };
        },
        methods: {
                initG6() {
                        const data = {
                                // 点集
                                nodes: [
                                        {
                                                id: 'master',
                                                label: 'master',
                                                size: '50',
                                                x: 60,
                                                y: 20,
                                        },
                                        {
                                                id: 'worker1',
                                                label: 'worker1',
                                                size: '50',
                                                x: 600,
                                                y: 200,
                                        },
                                        {
                                                id: 'worker2',
                                                label: 'worker2',
                                                size: '50',
                                                x: 1000,
                                                y: 600,
                                        },
                                        {
                                                id: 'sw1',
                                                label: 'sw1',
                                                size: '50',
                                                x: 800,
                                                y: 700,
                                        },
                                        {
                                                id: 'sw2',
                                                label: 'sw3',
                                                size: '50',
                                                x: 555,
                                                y: 900,
                                        },
                                        {
                                                id: 'sw3',
                                                label: 'sw3',
                                                size: '50',
                                                x: 30,
                                                y: 100,
                                        }
                                ],
                                // 边集
                                edges: [
                                        // 表示一条从 node1 节点连接到 node2 节点的边
                                        {
                                                source: 'master',
                                                target: 'sw3',
                                        },
                                        {
                                                source: 'worker1',
                                                target: 'sw1',
                                        },
                                        {
                                                source: 'worker2',
                                                target: 'sw2',
                                        },
                                        {
                                                source: 'sw1',
                                                target: 'sw2',
                                        },
                                        {
                                                source: 'sw1',
                                                target: 'sw3',
                                        },
                                        {
                                                source: 'sw2',
                                                target: 'sw3',
                                        }
                                ],
                        };

                        // 创建 G6 图实例
                        const graph = new G6.Graph({
                                container: 'mountNode', // 指定图画布的容器 id，与第 9 行的容器对应
                                // 画布宽高
                                width: 2150,
                                height: 1080,
                                renderer:'svg',
                                modes: {
                                        default: [
                                                'drag-canvas',
                                                'zoom-canvas',
                                                'drag-node',
                                                {
                                                        type: 'tooltip', // 提示框
                                                        formatText(model) {
                                                                // 提示框文本内容
                                                                const text = 'label: ' + model.label + '<br/> class: ' + model.class;
                                                                return text;
                                                        },
                                                },
                                        ], // 允许拖拽画布、放缩画布、拖拽节点
                                },
                                layout: {
                                        // Object，可选，布局的方法及其配置项，默认为 random 布局。
                                        type: 'force', // 指定为力导向布局
                                        linkDistance: 100,
                                        fitCenter: true,
                                        preventOverlap: true, // 防止节点重叠
                                        // nodeSize: 30        // 节点大小，用于算法中防止节点重叠时的碰撞检测。由于已经在上一节的元素配置中设置了每个节点的 size 属性，则不需要在此设置 nodeSize。
                                },
                                //fitView: true,//设置是否将图适配到画布中；
                                //fitViewPadding: [20, 40, 50, 20]//画布上四周的留白宽度。
                        });
                        // 读取 data 中的数据源到图上
                        graph.data(data);
                        // 渲染图
                        graph.render();
                }
        },
        mounted() {
                this.initG6()
        }
};
</script>