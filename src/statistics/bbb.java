//buttonAdd = new JButton("Add");
//        buttonAdd.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JLabel label = new JLabel(Integer.toString(count++));
//                labels.add(label);
//                hGroup.addComponent(label);
//                vGroup.addComponent(label);
//                label.revalidate();
//            }
//        });
//        buttonRemove = new JButton("Remove");
//        buttonRemove.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if ( labels.isEmpty() ) {
//                    return;
//                }
//                JLabel label = labels.pollFirst();
//                frame.remove(label);
//                frame.revalidate();
//                frame.repaint();
//            }
//        });
//        
//        
//        layout = new GroupLayout(frame.getContentPane());
////        hGroup = layout.createParallelGroup()
////                .addComponent(buttonAdd, 0, 0, Short.MAX_VALUE)
////                .addComponent(buttonRemove, 0, 0, Short.MAX_VALUE);
////        layout.setHorizontalGroup(hGroup);
////        vGroup = layout.createSequentialGroup()
////                .addComponent(buttonAdd)
////                .addComponent(buttonRemove);
////        layout.setVerticalGroup(vGroup);
//        hGroup = layout.createParallelGroup();
//        layout.setHorizontalGroup(
//            layout.createParallelGroup()
//            .addComponent(buttonAdd, 0, 0, Short.MAX_VALUE)
//            .addComponent(buttonRemove, 0, 0, Short.MAX_VALUE)
//            .addGroup(hGroup)
//        );
//        vGroup = layout.createSequentialGroup();
//        layout.setVerticalGroup(
//            layout.createSequentialGroup()
//            .addComponent(buttonAdd)
//            .addComponent(buttonRemove)
//            .addGroup(vGroup)
//        );
//        frame.getContentPane().setLayout(layout);

//    GroupLayout layout;
//    ParallelGroup hGroup;
//    SequentialGroup vGroup;
//    JButton buttonAdd;
//    JButton buttonRemove;
//    LinkedList<JLabel> labels = new LinkedList<JLabel>();
//    int count = 0;