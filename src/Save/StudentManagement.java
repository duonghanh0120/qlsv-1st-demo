package Save;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
public class StudentManagement extends JFrame implements ActionListener, MouseListener{
	Connection conn;
	Statement stm;
	ResultSet rst;
	
	// Tạo bảng chứa thôgn tin truy vấn từ csdl
	Vector vData=new Vector();
	Vector vTitle=new Vector();
	JScrollPane tableResult;
	DefaultTableModel model;
	JTable tb= new JTable();
	
	// Các nút thao tác với dữ liệu
	JButton edit,delete,insert;
	
	//Vị trí hàng đã chọn ở bảng dữ liệu
	int selectedrow=0;
	
	
	public StudentManagement(String s)	{
		super(s);
		try	{
			//Nạp Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Kết nối đến csdl và tạo đối tượng Statement
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/studentmanagement","root","0000");
			stm=conn.createStatement();
			//Tạo p để nhóm các nút và đặt vào dao diện chính
			JPanel p = new JPanel();
			//Tạo các nút và gắn ống nghe
			edit = new JButton("Edit");
			edit.addActionListener(this);
			delete = new JButton("Delete");
			delete.addActionListener(this);
			insert = new JButton("Insert");
			insert.addActionListener(this);
			//Đặt các nút vào JPanel p
			p.add(edit);
			p.add(delete);
			p.add(insert);
			//Đặt p vào vùng dưới của cửa sổ chính
			this.add(p,"South");
			//Nạp dữ liệu vào 2 Vector: vTitle(tên cột) và vData (các hang dữ liệu chuẩn bị tạo JTable
			reload();
			//Tạo bảng hiển thị thông tin lên cửa sổ
			model = new DefaultTableModel(vData,vTitle);
			tb=new JTable(model);
			//Gắn ống nghe khi ấn chuột chọn hàng
			tb.addMouseListener(this);
			tableResult=new JScrollPane(tb);
			this.getContentPane().add(tableResult,"North");
			//thiết lập kích thước, vị trí và hiển thị cửa sổ
			this.setSize(400,300);
			this.setLocation(200,100);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setVisible(true);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Đọc dữ liệu từ bảng students à điền dữ liệu vào hai vector: vTitle và vData để chuẩn bị xây dựng đối tượng JTable
	public void reload()	{
		try	{
			//Xoá hết dữ liệu hiện có trong 2 vector
			vTitle.clear();
			vData.clear();
			//Truy vấn dữ liệu từ bảng students
			ResultSet rst=stm.executeQuery("select * from students");
			//Tạo đối tượng rstmeta để lấy thông tin của ResultSet
			ResultSetMetaData rstmeta=rst.getMetaData();
			int num_col=rstmeta.getColumnCount();
			//Chuẩn bị dữ liệu để tạo bảng JTable. Tạo các tên cột cho bảng
			for (int i=1; i<=num_col;i++)	{
				vTitle.add(rstmeta.getColumnLabel(i));
			}
			//Tạo dữ liệu các hàng cho bảng (mỗi phần tử của Vector vData là một Vector)
			while (rst.next())	{
				Vector row=new Vector(num_col);
				for (int i=1; i<=num_col;i++) 
					row.add(rst.getString(i));
				vData.add(row);
				}
			rst.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
	}
	
	//Xoá dữ liệu khi người dùng chọn hàng và nhấn nút Delete
	public void delete() {
		try	{
			//Lấy nội dung hàng đã chọn
			Vector st=(Vector)vData.elementAt(selectedrow);
			//Tạo câu lệnh sql và xoá dữ liệu khỏi bảng students trong csdl
			String sql="delete from students where id = \""+st.elementAt(0)+"\"";
			stm.executeUpdate(sql);
			//Xoá nội dung hàng tương ứng trong vData
			vData.remove(selectedrow);
			//Cập nhật lại nội dung bảng hiển thị trên màn hình
			model.fireTableDataChanged();
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Xử lí khi người dùng ấn các nút trên cửa sổ
	public void actionPerformed(ActionEvent e)	{
	//Xử lí khi ấn nút Delete
	if (e.getActionCommand().equals("Delete"))	{
		delete();
	}
	//Xử lí khi ấn nút Insert
	if (e.getActionCommand().equals("Insert"))	{
		//Tạo cửa sổ để nhập mới
		new UpdateForm("Insert form",this,"","","0","0","0");
	}
	//Xử lí khi ấn nút Edit
	if (e.getActionCommand().equals("Edit"))	{
		//Lấy nội dung hàng đã chọn
		Vector st=(Vector)vData.elementAt(selectedrow);
		//Tạo cửa sổ hiệu chỉnh hàng đã chọn
		new UpdateForm("Edit form",this,(String)st.elementAt(0),(String)st.elementAt(1),(String)st.elementAt(2),
				(String)st.elementAt(3),(String)st.elementAt(4));
	}
}
	//Lấy vị trí hàng ở bảng JTable khi người dùng ấn chuột
	public void	mouseClicked(MouseEvent e)	{
		selectedrow=tb.getSelectedRow();
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public static void main(String[] args) {
		new StudentManagement("Student Management");
	}
}


class UpdateForm extends JFrame implements ActionListener {
		//Các thành phần trên giao diện
		JLabel namelb;
		JTextField name;
	
		JLabel mathlb;
		JTextField math;
		
		JLabel physlb;
		JTextField phys;
		
		JLabel chemlb;
		JTextField chem;
		
		//Dùng để hiện thị lỗi khi người dùng nhập dữ liệu sai
		JLabel errorlb;
		JLabel errordetails;
		//Hai nút để xác nhận và huỷ việc cập nhật thông tin
		JButton ok;
		JButton cancel;
		
		//Nhận đối tượng cửa sổ chính truyền đến
		StudentManagement mst;
		
		//Nhận id của một record của bảng students khi hiệu chỉnh
		String id;
		public UpdateForm(String s, StudentManagement st, String i, String na, String m, String ph,String ch)	{
			super(s);
			// Nhận đối tượng cửa sổ chính truyền đến để gọ hàm cập nhật dữ liệu dao diện cửa sổ chính và cập nhật csdl
			mst=st;
			//Tạo giao diện để cập nhật thông tin
			Container cont=this.getContentPane();
			cont.setLayout(new GridLayout(6,2));
			namelb=new JLabel("Name");
			name=new JTextField(na);
			cont.add(namelb);
			cont.add(name);
			
			mathlb=new JLabel("Math");
			math=new JTextField(m);
			cont.add(mathlb);
			cont.add(math);
			
			physlb=new JLabel("Physics");
			phys=new JTextField(ph);
			cont.add(physlb);
			cont.add(phys);

			chemlb=new JLabel("Chemistry");
			chem=new JTextField(ch);
			cont.add(chemlb);
			cont.add(chem);
			
			errorlb=new JLabel("");
			errordetails=new JLabel("");
			errorlb.setVisible(false);
			errordetails.setVisible(false);
			cont.add(errorlb);
			cont.add(errordetails);
			
			JButton ok= new JButton("OK");
			JButton cancel = new JButton("Cancel");
			cont.add(ok);
			cont.add(cancel);
			ok.addActionListener(this);
			cancel.addActionListener(this);
			
			this.setSize(230,300);
			this.setLocation(250,100);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setVisible(true);
			//Nhận id được truyền đến từ cửa sổ chính phục vụ việc cập nhật thông tin vào csdl
			id=i;
		}
		
		//Gọi khi ấn nút OK hoặc cancel
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("OK"))	{
				//Gọi hàm cập nhật dữ liệu
				insertDB();
			} else this.dispose();
			//Tắt cửa sổ khi ấn Cancel
		}
		public void insertDB() {
			//Nếu có một trường hợp không có dữ liệu thì báo lỗi
			if(name.getText().equals("") || math.getText().equals("") || phys.getText().equals("") || chem.getText().equals(""))
			{
				//Tạo nội dung lỗi
				errorlb.setText("Error");
				errordetails.setText("empty value");
				errorlb.setForeground(Color.RED);
				errordetails.setForeground(Color.RED);
				
				//Hiển thị lỗi
				errorlb.setVisible(true);
				errordetails.setVisible(true);
			} else	{
				try	{
					//Lấy nội dung đã nhập ở giao diện
					String na=name.getText();
					float m= Float.parseFloat(math.getText());
					float ph= Float.parseFloat(phys.getText());
					float ch= Float.parseFloat(chem.getText());
					String sql="";
					//Nếu là nhập mới
					if (this.getTitle().equals("Insert form"))
						sql="insert into students(Name,Math,Phys,Chem,Aver) values (\""+na+"\","+m+","+ph+","+ch+","+(m+ph+ch)/3+")";
					else //Nếu là hiệu chỉnh
						sql="update students set Name = \""+na+"\",Math="+m+",Phys="+ph+",Chem="+ch+",Aver="+(m+ph+ch)/3+"where ID=\""+id+"\"";
					//Cập nhật vào csdl
					mst.stm.executeUpdate(sql);
					//Cập nhật giao diện cửa sổ chính
					mst.reload();
					mst.model.fireTableDataChanged();
					//Tắt cửa sổ
					this.dispose();
				} catch	(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
}


